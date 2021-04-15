//dupa exemplul din laborator

#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<math.h>
#include"mpi.h"

#define RANDOM_SEED 42
#define LOCAL_RANDOM_MAX 2500

int mat_dim = 100;
int proc_number = 1;


int** allocate_matrix(int dim){
    int **a = (int**)calloc(dim, sizeof(int*));
    if(a == NULL){
        printf("Could not allocate memodey\n");
        exit(1);
    }

    for(int i=0;i<dim;i++){
        a[i] = (int*)calloc(dim, sizeof(int));
        if(a == NULL){
            printf("Could not allocate memodey\n");
            exit(1);
        }
    }
    return a;
}

int* allocate_array(int len){
    int *arr = (int*)calloc(len, sizeof(int));
    
    if(arr == NULL){
        printf("Could not allocate memodey\n");
        exit(1);
    }

    return arr;
}

void free_matrix(int **a, int dim){
    for(int i =0;i<dim;i++){
        free(a[i]);
    }
    free(a);
}

int print_matrix(int** a, int dim){
    for(int i = 0;i<dim;i++){
        for(int j = 0;j<dim;j++){
            printf("%d ", a[i][j]);
        }
        printf("\n");
    }
}

void mul_block(int len, int* a, int* b, int* c){
    for(int i = 0; i<len; i++){
        for(int j =0; j<len; j++){
            c[i * len + j] = 0;
            for(int k = 0;k<len; k++){
                c[i * len + j]+= a[i * len + k]* b[k * len + j];
            }
        }
    }
}

void add_block(int len, int* a, int* b, int* c){
    for(int i = 0; i<len; i++){
        for(int j =0; j<len; j++){
            int k = i * len + j;
            c[k] = a[k] + b[k];
        }
    }
}

void populate_matrix(int** mat, int dim){
    srand(RANDOM_SEED);

    for(int i = 0;i<dim;i++){
        for(int j =0;j<dim;j++){
            mat[i][j] = rand() % LOCAL_RANDOM_MAX;
        }
    }
}

void populate_flat_matrix(int* mat, int dim){
    srand(RANDOM_SEED);

    for(int i = 0;i<dim;i++){
        for(int j =0;j<dim;j++){
            mat[i * dim + j] = rand() % LOCAL_RANDOM_MAX;
        }
    }
}



int main(int argc, char** args){

    int process_id;
    int *a;
    int *b;
    int *initial_result;
    int *result_matrix;

    mat_dim = atoi(args[argc - 1]);
    int flat_mat_dimension = mat_dim*mat_dim;

    a = allocate_array(flat_mat_dimension);
    b = allocate_array(flat_mat_dimension);
    result_matrix = allocate_array(flat_mat_dimension);
    initial_result = allocate_array(flat_mat_dimension);

    MPI_Status Stat; 
	MPI_Init(&argc, &args); 
	MPI_Comm_size(MPI_COMM_WORLD, &proc_number);
    MPI_Comm_rank(MPI_COMM_WORLD, &process_id);

    int process_number_sqrt = (int)sqrt(proc_number);

    // running conditions
    if(argc > 1){
        if(mat_dim % process_number_sqrt != 0){
            printf("Matrix_size [arg1] must be divizible with sqrt(number_of_processes)[arg2]\n");
            exit(1);
        }
    }

    if (proc_number != process_number_sqrt * process_number_sqrt){
		printf("Setati %d procese !\n", process_number_sqrt * process_number_sqrt);
		exit(1);
	}


    // the root process initializes matrices
    if(process_id == 0){
 

        populate_flat_matrix(a, mat_dim);
        populate_flat_matrix(b, mat_dim);        
    }
    
    // the new arrays are inilialized to 0 thanks to calloc
    int* matrix_a_block = allocate_array(flat_mat_dimension);
    int* matrix_b_block = allocate_array(flat_mat_dimension);

    int* block_matrix_product = allocate_array(flat_mat_dimension);
    int* block_matrix_result = allocate_array(flat_mat_dimension);

    // broadcast a and b to everyone
    MPI_Bcast(a, flat_mat_dimension, MPI_INT, 0, MPI_COMM_WORLD);
	MPI_Bcast(b, flat_mat_dimension, MPI_INT, 0, MPI_COMM_WORLD);

    // parametrii pt efectuarea calculelor
    int block_size_R = mat_dim / process_number_sqrt;
    int target_line = process_id / process_number_sqrt;
    int target_column = process_id % process_number_sqrt;


    // individual process workload
    for(int i = 0; i < process_number_sqrt; i++){

        //copy block a
        for(int l = target_line * process_number_sqrt; 
                l < (target_line + 1) * process_number_sqrt; 
                l++){
            
            for(int c = i * process_number_sqrt; 
                    c < (i + 1) * process_number_sqrt; 
                    c++){
                
                int a_block_idx = (l - (target_line * process_number_sqrt)) * process_number_sqrt
                     + c - (i * process_number_sqrt);
                
                matrix_a_block[a_block_idx] = a[l * mat_dim + c];

            }

        }

        //copy block b
        for(int l = i * process_number_sqrt; 
                l < (target_line + 1) * process_number_sqrt; 
                l++){
            
            for(int c = target_column * process_number_sqrt; 
                    c < (target_column + 1) * process_number_sqrt; 
                    c++){

                int b_block_idx = (l - (i * process_number_sqrt)) * process_number_sqrt
                     + c - (target_column * process_number_sqrt);
                
                matrix_b_block[b_block_idx] = b[l * mat_dim + c];

            }

        }


        mul_block(process_number_sqrt, matrix_a_block, matrix_b_block, block_matrix_product);
        add_block(process_number_sqrt, block_matrix_result, block_matrix_product, block_matrix_result);
    }

    // wait for others to finish
    MPI_Barrier(MPI_COMM_WORLD);

    //trimiterea rezultatelor partiale si acumularea lor in buffer-ul din procesul 0
    MPI_Gather(block_matrix_result, proc_number, MPI_INT, initial_result, proc_number, MPI_INT, 0, MPI_COMM_WORLD);

    if(process_id == 0){
        
        for(int i =0; i<mat_dim; i++){
            for(int j =0; j<mat_dim;j++){
                int matrix_elm_idx = i * mat_dim + j;
                int block = matrix_elm_idx / (block_size_R * block_size_R);
                
                int block_line = block / process_number_sqrt;
                int final_line = block_line * block_size_R + 
                    (matrix_elm_idx % (block_size_R * block_size_R)) / block_size_R;
                
                int block_column = block % process_number_sqrt;
                int final_column = block_column * block_size_R + 
                    (matrix_elm_idx % (block_size_R * block_size_R)) % block_size_R;

                result_matrix[final_line * mat_dim + final_column] = initial_result[matrix_elm_idx];
            }
        }
    }

    free(a);
    free(b);
    free(matrix_a_block);
    free(matrix_b_block);
    free(block_matrix_product);
    free(block_matrix_result);
    free(result_matrix);
    
    MPI_Finalize();
    return 0;
}
