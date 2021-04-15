/* Calculates the PI. It is the area of f(x)=4/(1+x^2) in interval from 0 to 1.
The interval in splitted into n intervals which are distributed equaly to all 
available processes. The result consists in a sum of areas calculated by each 
processor !!! */

#include <stdio.h>
#include <math.h>
#include "mpi.h"

/* Function definition */
double f( double a ) {
    return (4.0 / (1.0 + a*a));
}

int main( int argc, char *argv[]) {
    int done = 0, n, myid, numprocs, i;
    double PI25DT=3.141592653589793238462643;
    double mypi, pi, h, sum, x;
    double startwtime=0.0, endwtime;
    int  namelen;
    char processor_name[MPI_MAX_PROCESSOR_NAME];

    /* Initialize MPI */
    MPI_Init(&argc,&argv);
    
    /* Get no. of processes */
    MPI_Comm_size(MPI_COMM_WORLD,&numprocs);
    
    /* Get current process ID */
    MPI_Comm_rank(MPI_COMM_WORLD,&myid);
    
    /* Get current processor name */
    MPI_Get_processor_name(processor_name,&namelen);

    /* Print info about MPI WORLD and about current process */
    printf("MPI_WORLD has %d processes\n", numprocs);
    printf("Process %d on %s\n",myid, processor_name);

    /* Initialize no. of intervals to 0 */
    n = 0;
    while (!done) {
        /* What root process does: */
	if (myid==0) {
	
            /* Read no. of intervals prom keyboard */
	    printf("Enter the number of intervals: (0 quits) ");
            scanf("%d",&n);

    	    /* Next line should only be used if you are too lazy to enter no.
	    of intervals */
	    /* if (n==0) n=100; else n=0; */

	    /* Get current time */
	    startwtime = MPI_Wtime();
        }
	
	/* Send no. of intervals to all processes. Only root sends the message
	and the others receive the message */
        MPI_Bcast(&n, 1, MPI_INT, 0, MPI_COMM_WORLD);
        
	/* In no intervals defined then stop program */
	if (n==0)
            done=1;
        /* If intervals defined */
	else {
            /* Calculate f(x) where x=1/n*(i-0.5)*/
	    h=1.0/(double)n;
            sum=0.0;
            for (i=myid+1;i<=n;i+=numprocs) {
                x=h*((double)i-0.5);
                sum+=f(x);
            }
	    
	    /* Area is 1/n*f(x) */
            mypi=h*sum;

            /* Send calculated area to root through a SUM function. I don't 
	    know yet if root sends it's value, but it's for sure he receives
	    as result the SUM of all numbers from the other processes including 
	    his own. */
	    MPI_Reduce(&mypi, &pi, 1, MPI_DOUBLE, MPI_SUM, 0, MPI_COMM_WORLD);

            /* What root does */
	    if (myid==0)
	    {
                /* Print the result of PI*/
		printf("pi is approximately %.16f, Error is %.16f\n",
                       pi, fabs(pi - PI25DT));
		       
		/* Get current time and print total time needed */
		endwtime = MPI_Wtime();
		printf("wall clock time = %f\n",
		       endwtime-startwtime);	       
	    }
        }
    }
    
    /* Terminate MPI */
    MPI_Finalize();

    /* Success */
    return 0;
}