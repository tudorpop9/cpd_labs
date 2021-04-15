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

typedef struct {
   int      Size;     /* The number of processors. (Size = q_proc*q_proc)
  */
   int      p_proc;        /* The number of processors in a row (column).
*/
   int      Row;      /* The mesh row this processor occupies.        */
   int      Col;      /* The mesh column this processor occupies.     */
   int      MyRank;     /* This processors unique identifier.           */
   MPI_Comm Comm;     /* Communicator for all processors in the mesh. */
   MPI_Comm Row_comm; /* All processors in this processors row   .    */
   MPI_Comm Col_comm; /* All processors in this processors column.    */
} MESH_INFO_TYPE;

void SetUp_Mesh(MESH_INFO_TYPE *grid) {

   int Periods[2];      /* For Wraparound in each dimension.*/
   int Dimensions[2];   /* Number of processors in each dimension.*/
   int Coordinates[2];  /* processor Row and Column identification */
   int Remain_dims[2];      /* For row and column communicators */


 /* MPI rank and MPI size */
   MPI_Comm_size(MPI_COMM_WORLD, &(grid->Size));
   MPI_Comm_rank(MPI_COMM_WORLD, &(grid->MyRank));

 /* For square mesh */
   grid->p_proc = (int)sqrt((double) grid->Size);             
	if(grid->p_proc * grid->p_proc != grid->Size){
	 MPI_Finalize();
	 if(grid->MyRank == 0){
	 printf("Number of Processors should be perfect square\n");
	 }
		 exit(-1);
	}

   Dimensions[0] = Dimensions[1] = grid->p_proc;

   /* Wraparound mesh in both dimensions. */
   Periods[0] = Periods[1] = 1;    

   /*  Create Cartesian topology  in two dimnesions and  Cartesian 
       decomposition of the processes   */
   MPI_Cart_create(MPI_COMM_WORLD, 2, Dimensions, Periods, 1, &(grid->Comm));
   MPI_Cart_coords(grid->Comm, grid->MyRank, 2, Coordinates);

   grid->Row = Coordinates[0];
   grid->Col = Coordinates[1];

 /*Construction of row communicator and column communicators 
 (use cartesian row and columne machanism to get Row/Col Communicators)  */

   Remain_dims[0] = 0;            
   Remain_dims[1] = 1; 

 /*The output communicator represents the column containing the process */
   MPI_Cart_sub(grid->Comm, Remain_dims, &(grid->Row_comm));
   
   Remain_dims[0] = 1;
   Remain_dims[1] = 0;

 /*The output communicator represents the row containing the process */
   MPI_Cart_sub(grid->Comm, Remain_dims, &(grid->Col_comm));
}

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
    int **a;
    int **b;
    int **c;
    int *a_block_sample, *b_block_sample;
    int *flat_a, *flat_b;
    int source, destination, send_tag, recv_tag, Bcast_root;

    int *initial_result;
    int *result_matrix_arr;
    int block_size = 0, flat_block_size = 0;

    mat_dim = atoi(args[argc - 1]);
    int flat_mat_dimension = mat_dim*mat_dim;

    a = allocate_matrix(mat_dim);
    b = allocate_matrix(mat_dim);
    
    // initial_result = allocate_matrix(mat_dim);
    MESH_INFO_TYPE grid;
    MPI_Status status; 

	MPI_Init(&argc, &args);
    SetUp_Mesh(&grid); 
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
        populate_matrix(a, mat_dim);
        populate_matrix(b, mat_dim);        
    }


    block_size = mat_dim / process_number_sqrt;
    flat_block_size = block_size * block_size;

    a_block_sample = allocate_array(flat_block_size);
    b_block_sample = allocate_array(flat_block_size);

    flat_a = allocate_array(flat_mat_dimension);
    flat_b = allocate_array(flat_mat_dimension);


    if(process_id == 0){
        int proc_id = 0, global_row_idx = 0, global_col_idx = 0, local_idx = 0;
        for(int ip = 0; ip < process_number_sqrt; ip++){
            for(int jp = 0; jp < process_number_sqrt; jp++){
                proc_id = ip * process_number_sqrt + jp;

                for(int irow = 0; irow < block_size; irow++){
                    global_row_idx = ip * block_size + irow;
                    for(int jcol = 0; jcol < block_size; jcol++){
                        local_idx = ip * flat_block_size + irow * block_size + jcol;
                        global_col_idx = jp * block_size + jcol;

                        flat_a[local_idx] = a[global_row_idx][global_col_idx];
                        flat_b[local_idx] = b[global_row_idx][global_col_idx];
                    }
                } 
            }
        }
    }

    MPI_Barrier(MPI_COMM_WORLD);

    //Sends data to everyone
    MPI_Scatter(flat_a, flat_block_size, MPI_INT, a_block_sample, flat_block_size, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Scatter(flat_b, flat_block_size, MPI_INT, b_block_sample, flat_block_size, MPI_INT, 0, MPI_COMM_WORLD);

     /* Do initial arrangement of Matrices */
    // No Idea what is happening here, but it generates segmentation fault
    if(grid.Row !=0){
        source   = (grid.Col + grid.Row) % grid.p_proc;
        destination = (grid.Col + grid.p_proc - grid.Row) % grid.p_proc; 
        recv_tag =0;
        send_tag = 0;
        MPI_Sendrecv_replace(a_block_sample, flat_mat_dimension, MPI_INT,
            destination, send_tag, source, recv_tag, grid.Row_comm, &status);
    }
    if(grid.Col !=0){
        source   = (grid.Row + grid.Col) % grid.p_proc;
        destination = (grid.Row + grid.p_proc - grid.Col) % grid.p_proc; 
        recv_tag =0;
        send_tag = 0;
        MPI_Sendrecv_replace(b_block_sample, flat_mat_dimension, MPI_INT,
            destination,send_tag, source, recv_tag, grid.Col_comm, &status);
    }

    initial_result = allocate_array(flat_block_size);

    send_tag = 0;
    recv_tag = 0;
    for(int istage=0; istage<grid.p_proc; istage++){
        int index=0;
        for(int irow=0; irow<block_size; irow++){
            for(int icol=0; icol<block_size; icol++){
                for(int jcol=0; jcol<block_size; jcol++){
                    initial_result[index] += a_block_sample[irow*block_size + jcol] *
                    b_block_sample[jcol*block_size + icol];
                }
                index++;
            }
        }
        /* Move Bloc of Matrix A by one position left with wraparound */
        source   = (grid.Col + 1) % grid.p_proc;
        destination = (grid.Col + grid.p_proc - 1) % grid.p_proc; 
        MPI_Sendrecv_replace(a_block_sample, flat_block_size, MPI_INT,
                destination, send_tag, source, recv_tag, grid.Row_comm, &status);

        /* Move Bloc of Matrix B by one position upwards with wraparound */
        source   = (grid.Row + 1) % grid.p_proc;
        destination = (grid.Row + grid.p_proc - 1) % grid.p_proc; 
        MPI_Sendrecv_replace(b_block_sample, flat_block_size, MPI_INT,
                destination, send_tag, source, recv_tag, grid.Col_comm, &status);
    }

    if(process_id == 0){
        result_matrix_arr = allocate_array(flat_block_size);
    }

    MPI_Barrier(MPI_COMM_WORLD);

    //trimiterea rezultatelor partiale si acumularea lor in buffer-ul din procesul 0
    MPI_Gather (initial_result, flat_block_size, MPI_INT,
            result_matrix_arr, flat_block_size, MPI_INT, 0, grid.Comm);

    if(process_id == 0){
        c = allocate_matrix(mat_dim);
        int proc_id = 0, global_row_idx = 0, global_col_idx = 0, local_idx = 0;
        for(int ip = 0; ip < process_number_sqrt; ip++){
            for(int jp = 0; jp < process_number_sqrt; jp++){
                proc_id = ip * process_number_sqrt + jp;

                for(int irow = 0; irow < block_size; irow++){
                    global_row_idx = ip * block_size + irow;
                    for(int jcol = 0; jcol < block_size; jcol++){
                        local_idx = ip * flat_block_size + irow * block_size + jcol;
                        global_col_idx = jp * block_size + jcol;

                        c[global_row_idx][global_col_idx] = result_matrix_arr[local_idx];
                    }
                } 
            }
        }

        printf("\nFinshed\n");
    }

    free_matrix(a, mat_dim);
    free_matrix(b, mat_dim);
    free_matrix(c, mat_dim);
    free(a_block_sample);
    free(b_block_sample);
    free(flat_a);
    free(flat_b);
    free(initial_result);
    free(result_matrix_arr);

    MPI_Finalize();
    return 0;
}