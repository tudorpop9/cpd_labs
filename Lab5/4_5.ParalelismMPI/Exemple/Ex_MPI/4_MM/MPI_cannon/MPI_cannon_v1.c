#include "mpi.h"
#include <math.h>
#include <stdio.h>
#include <iostream.h>
#include <malloc.h>
#include <windows.h>

// Autor: Ulrich Zoltan, grupa 3241, mai 2006
// latura matricii

#define MatrixSize 16

void main(int argc,char **argv)
{
   int myrank, i, j, dimBloc, irez, jrez;
   int numprocs;
   int *A,*B,*C;

/* if(argc!=1)
   {
	   printf("usage: mpiexec -n NumarProcese cannon MatrixSize\n");
	   exit(0);
   }

   int MatrixSize = atoi(argv[1]);
   if(MatrixSize<1)
   {
	   printf("Invalid MatrixSize\n");
	   exit(0);
   }
      
   int matrixSize = MatrixSize;*/

   // facem enroll
   MPI_Init(&argc,&argv);
   MPI_Comm_rank(MPI_COMM_WORLD,&myrank);
   MPI_Comm_size(MPI_COMM_WORLD,&numprocs);

   // procesul master nu calculeaza blocuri de matrici
   numprocs--;

   // verificam daca numarul de procesoare este valid
 /*  if(((float)sqrt(numprocs))!=((int)sqrt(numprocs)) || numprocs<1)
   {
	   printf("Numarul de procese trebuie sa fie un patrat perfect + 1\n");
	   MPI_Finalize();
	   exit(0);
   }

   // verificam daca latura matricii este corecta
   if(MatrixSize%((int)sqrt(numprocs))!=0)
   {
	   printf("Latura matricii trebuie sa fie divizibila cu sqrt(NumarProcese)\n");
	   printf("pentru ca fiecare proces sa primeasca un bloc patratic de calculat.\n");
	   MPI_Finalize();
	   exit(0);
   }*/

   // asteptam restul proceselor
   MPI_Barrier(MPI_COMM_WORLD);
      
   // daca suntem master
   if(myrank==0)
   {
	   A = (int*)malloc(MatrixSize*MatrixSize*sizeof(int));
	   B = (int*)malloc(MatrixSize*MatrixSize*sizeof(int));
	   C = (int*)malloc(MatrixSize*MatrixSize*sizeof(int));

	   printf("MyRank = %d\n", myrank);
	   printf("NumProcs = %d\n", numprocs);

		// initializare matrici
		for (i = 0; i < MatrixSize; i++)
		{
			for (j = 0; j < MatrixSize; j++) 
			{
				*(A + i*MatrixSize + j) = i==j;               // A va fi matricea identitate
				*(B + i*MatrixSize + j) = rand()%10;
				*(C + i*MatrixSize + j) = 0;
			}
		}
   
		// afisam matricea A
		printf("\nMatricea A:\n");
		for(i=0;i<MatrixSize;i++)
		{
			for(j=0;j<MatrixSize;j++)
			{
				printf("%d ",*(A + i*MatrixSize + j));
			}
			printf("\n");
		}

		// afisam matricea B
		printf("\nMatricea B:\n");
		for(i=0;i<MatrixSize;i++)
		{
			for(j=0;j<MatrixSize;j++)
			{
				printf("%d ",*(B + i*MatrixSize + j));
			}
			printf("\n");
		}
		printf("\n");

		dimBloc = MatrixSize / (int)(sqrt(numprocs));
		//int *rez = (int*)malloc(dimBloc*dimBloc*sizeof(int));

		// cate blocuri pe linie
		int nBlocLinie = MatrixSize/dimBloc;
		// cate blocuri pe coloana
		int nBlocColoana = MatrixSize/dimBloc;
		// in implementarea curenta, vor avea acceasi valoare

		// buffer temporar pentru shiftarea matricii A
		int *templine = (int*)malloc(nBlocLinie*dimBloc*dimBloc*sizeof(int));
		int decalaj,ndecalaj;

		// shiftam matricea A, prima linie nu necesita shiftare
		// o shiftare initiala a matricii este necesara
		// fiecare linie (de blocuri) va avea blocurile shiftate la stanga cu un numar de pozitii
		// egal cu numarul liniei, numerotarea incepand de la 0
		// ex: pentru matricea (cu 2x2 blocuri)      rezultatul shiftarii:
		//
		// 01 02 03 04                        01 02 03 04
		// 05 06 07 08                        05 06 07 08
		// 09 10 11 12                        11 12 09 10
		// 13 14 15 16                        15 16 13 14
		for(i=1;i<nBlocLinie;i++)
		{
			// calculam decalajul
			decalaj = -i*dimBloc;
			for(j=0;j<nBlocColoana;j++)
			{
				// daca se depasesc limitele matricii ajustam
				ndecalaj = decalaj + (j<i)*MatrixSize;
				for(int k=0;k<dimBloc;k++)
				{
					// copiem fiecare bloc pe pozitia corespunzatoare shiftata din buffer
					memcpy(templine+k*MatrixSize+j*dimBloc+ndecalaj,A+(i*dimBloc+k)*MatrixSize+j*dimBloc,dimBloc*sizeof(int));
				}
			}
			// copiem bufferul (cu linia shiftata) inapoi in matrice
			memcpy(A + (i*dimBloc*MatrixSize),templine,MatrixSize*dimBloc*sizeof(int));
		}
		// eliberam bufferul
		free(templine);

		// alocam bufferul temporar pentru a pastra coloanele de blocuri shiftate.
		int *tempcol = (int*)malloc(nBlocColoana*dimBloc*dimBloc*sizeof(int));

		// shiftam matricea B, prima coloana nu necesita shiftare
		// o shiftare initiala a matricii este necesara
		// fiecare coloana (de blocuri) va avea blocurile shiftate in sus cu un numar de pozitii
		// egal cu numarul coloanei, numerotarea incepand de la 0
		// ex: pentru matricea (cu 2x2 blocuri)      rezultatul shiftarii:
		//
		// 01 02 03 04                        01 02 11 12
		// 05 06 07 08                        05 06 15 16
		// 09 10 11 12                        09 10 03 04
		// 13 14 15 16                        13 14 07 08
		for(j=1;j<nBlocColoana;j++)
		{
			// calculam decalajul
			decalaj = -j*dimBloc*dimBloc;
			for(i=0;i<nBlocLinie;i++)
			{
				// daca se depasesc limitele matricii ajustam
				ndecalaj = decalaj + (j>i)*nBlocColoana*dimBloc*dimBloc;
				for(int k=0;k<dimBloc;k++)
				{
					// copiem blocurile in buffer pe pozitiile shiftate
					memcpy(tempcol+(i*dimBloc+k)*dimBloc+ndecalaj,B+(i*dimBloc+k)*MatrixSize+j*dimBloc,dimBloc*sizeof(int));
				}
			}
			// copiem bufferul inapoi in matricea originala
			for(int k=0;k<MatrixSize;k++)
			{
				memcpy(B + k*MatrixSize + j*dimBloc,tempcol+k*dimBloc,dimBloc*sizeof(int));
			}
		}
		// eliberam bufferul
		free(tempcol);

		for(i=1;i<=numprocs;i++)
		{
			MPI_Send(A,MatrixSize*MatrixSize,MPI_INT,i,1,MPI_COMM_WORLD);
			MPI_Send(B,MatrixSize*MatrixSize,MPI_INT,i,1,MPI_COMM_WORLD);
		}
	}

	// slavii asteapta dupa master
	MPI_Barrier(MPI_COMM_WORLD);

	// daca NU suntem master
	if(myrank!=0)
	{
		// alocam spatiu pentru receptia matricilor
		A = (int*)malloc(MatrixSize*MatrixSize*sizeof(int));
		B = (int*)malloc(MatrixSize*MatrixSize*sizeof(int));

		// calculam dimensiunea unui bloc
		dimBloc = MatrixSize/((int)(sqrt(numprocs)));

		// alocam spatiu pentru matricea calculata de acest proces si o initializam la 0
		int *rez = (int*)malloc(dimBloc*dimBloc*sizeof(int));
		ZeroMemory(rez,dimBloc*dimBloc*sizeof(int));
   
		// receptionam matricile
		MPI_Status status;
		MPI_Recv(A,MatrixSize*MatrixSize,MPI_INT,0,1,MPI_COMM_WORLD,&status);
		MPI_Recv(B,MatrixSize*MatrixSize,MPI_INT,0,1,MPI_COMM_WORLD,&status);

		// calculam numarul de blocuri pe o dimensiune
		int nBlocuri = MatrixSize/dimBloc;
   
		// calculam numarul de linie si de coloana al blocului corespunzator
		// acestui proces in functie de id-ul din grupul creat
		int linie = (myrank-1) / nBlocuri;
		int coloana = (myrank-1) % nBlocuri;

   
		// definim si initializam coordonatele de start ale blocurilor pe care le inmultim
		int xa,ya,xb,yb;
		xa = xb = linie * dimBloc;
		ya = yb = coloana * dimBloc;
   
		// facem inmultirea matricilor, schimband coordonatele in loc sa shiftam matricile
		for(int k=0;k<nBlocuri;k++)
		{
			for(irez = 0; irez < dimBloc; irez++)
			{
				for(jrez = 0; jrez < dimBloc; jrez++)
				{
					for (i = 0;i < dimBloc; i++)
					{
						*(rez + irez*dimBloc + jrez) += (*(A+(xa+irez)*MatrixSize+(ya+i)))*(*(B+(xb+i)*MatrixSize+(yb+jrez)));
					}
				}
			}
			// facem actualizarea coordonatelor
			ya = (ya + dimBloc)%MatrixSize;
			xb = (xb + dimBloc)%MatrixSize;
		}

		MPI_Send(&linie,1,MPI_INT,0,2,MPI_COMM_WORLD);
		MPI_Send(&coloana,1,MPI_INT,0,2,MPI_COMM_WORLD);
		MPI_Send(rez,dimBloc*dimBloc,MPI_INT,0,2,MPI_COMM_WORLD);
   
		// eliberam matricile alocate
		free(rez);
	}

	// daca suntem master primim rezultatele
	if(myrank==0)
	{
		MPI_Status status;
		int *rez = (int*)malloc(dimBloc*dimBloc*sizeof(int));
		
		// bucla in care receptionam rezultatele de la fiecare sclav in parte
		for (int k = 1; k <= numprocs; k++)
		{
			MPI_Recv(&irez,1,MPI_INT,k,2,MPI_COMM_WORLD,&status);
			MPI_Recv(&jrez,1,MPI_INT,k,2,MPI_COMM_WORLD,&status);
			MPI_Recv(rez,dimBloc*dimBloc,MPI_INT,k,2,MPI_COMM_WORLD,&status);
			      
			// copiem matricile primite pe pozitiile corespunzatoare
			for (i = 0; i < dimBloc; i++)
			{
				for (j = 0; j < dimBloc; j++)
				{
					*(C + (irez*dimBloc+i)*MatrixSize + jrez*dimBloc+j) = *(rez + i*dimBloc + j);
				}
			}
		}

		// afisam matricea rezultat
		printf("Matricea C:\n");
		for(i=0;i<MatrixSize;i++)
		{
			for(j=0;j<MatrixSize;j++)
			{
				printf("%d ",*(C + i*MatrixSize + j));
			}
			printf("\n");
		}
		free(rez);
	}
	
	// eliberam matricile
	free(A);
	free(B);
	if(myrank==0) free(C);
	
	MPI_Finalize();
}