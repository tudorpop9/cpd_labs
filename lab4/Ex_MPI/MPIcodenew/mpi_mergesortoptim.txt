#include <iostream>
#include <mpi.h>
#include <vector>
#include <time.h>
#include <stdint.h>

using namespace std;

// Local merging of sorted sequences. Merges the sequences from
// begin1 to end1 with that from begin2 to end2; the result is stored
// starting at merged (which must have enough space)
void merge(int* begin1, int* end1, int* begin2, int* end2, int* merged)
{
    int* curr1 = begin1;
    int* curr2 = begin2;
    while(curr1<end1 || curr2<end2) {
        if(curr2>=end2 || (curr1<end1 && *curr1<*curr2)) {
            *merged = *curr1;
            ++curr1;
        } else {
            *merged = *curr2;
            ++curr2;
        }
        ++merged;
    }
}

// Recursively executes merge-sort for the sequence of sequence from in and of size n.
// If nrProcs==1, all happens locally; otherwise, half of the input sequence is sent
// to a child process for sorting and the other half is sorted by recursively calling
// mergeSortRec()
void mergeSortRec(size_t me, size_t n, int* in, int* buf, bool& isResultInInput, size_t nrProcs)
{
    if(n <= 1) {
        isResultInInput = true;
        return;
    }
    size_t k = n/2;
    bool r1;
    bool r2;
    if(nrProcs >= 2) {
        size_t child = me + nrProcs/2;
        cout << "Worker " << me <<", sending to child " << child << ", part size = " << n - k << "\n";
        MPI_Send(in + k, n - k, MPI_INT, child, 1, MPI_COMM_WORLD);
        mergeSortRec(me, k, in, buf, r1, nrProcs/2);
        r2 = r1;
        MPI_Status status;
        MPI_Recv((r1 ? in : buf) + k, n - k, MPI_INT, child, 1, MPI_COMM_WORLD, &status);
        cout << "Worker " << me <<", received from child " << child << ", part size = " << n - k << "\n";
    } else {
        mergeSortRec(me, k, in, buf, r1, 1);
        mergeSortRec(me, n-k, in+k, buf+k, r2, 1);
    }
    if(r2 != r1){
        if(r2){
            copy(in+k, in+n, buf+k);
        } else {
            copy(buf+k, buf+n, in+k);
        }
    }
    if(r1) {
        merge(in, in+k, in+k, in+n, buf);
        isResultInInput = false;
    } else {
        merge(buf, buf+k, buf+k, buf+n, in);
        isResultInInput = true;
    }
}

// Top function that calls mergeSortRec(). It must be called only on process 0
void mergeSort(vector<int>& v, size_t nrProcs)
{
    vector<int> buf;
    buf.resize(v.size());
    bool isResultInInput = true;
    mergeSortRec(0, v.size(), v.data(), buf.data(), isResultInInput, nrProcs);
    if(!isResultInInput) {
        copy(buf.begin(), buf.end(), v.begin());
    }
}

// A function that determines the parent of the current process (the process that
// will give the part to be sorted. It also determines the size of the part to be sorted
// and executes the sorting (receives the data, calls mergeSortRec() to do the actual work,
// and finally sends back the result.
void mergeWorker(size_t n, size_t me, size_t nrProcs)
{
    // find my position in the hierarchy
    size_t base = 0;
    size_t parent;
    size_t offset = me;
    while(offset > 0) {
        parent = base;
        size_t mid = nrProcs / 2;
        if(offset < mid) {
            n = n / 2;
            nrProcs = nrProcs / 2;
        } else {
            offset = offset - mid;
            n = n / 2 + n % 2;
            nrProcs = nrProcs / 2 + nrProcs % 2;
            base += mid;
        }
    }
    cout << "Worker " << me <<", child of " << parent << ", part size = " << n << "\n";
    
    // receive the data from the parent
    vector<int> v;
    v.resize(n);
    MPI_Status status;
    MPI_Recv(v.data(), n, MPI_INT, parent, 1, MPI_COMM_WORLD, &status);
    cout << "Worker " << me <<", received from parent " << parent << ", part size = " << n << "\n";

    // do the local sorting
    bool isResultInInput = true;
    vector<int> buf;
    buf.resize(n);
    mergeSortRec(me, v.size(), v.data(), buf.data(), isResultInInput, nrProcs);
    
    // send back the result to the parent
    cout << "Worker " << me <<", sending to parent " << parent << ", part size = " << n << "\n";
    MPI_Ssend((isResultInInput ? v.data() : buf.data()), n, MPI_INT, parent, 1, MPI_COMM_WORLD);
}

void generate(vector<int>& v, size_t n)
{
    v.reserve(n);
    for(size_t i=0 ; i<n ; ++i) {
        // v.push_back(rand());
        v.push_back((i*101011) % 123456);
    }
}

bool isSorted(vector<int> const& v)
{
    size_t const n = v.size();
    for(size_t i=1 ; i<n ; ++i) {
        if(v[i-1]>v[i]) return false;
    }
    return true;
}

int main(int argc, char** argv)
{
    MPI_Init(0, 0);
    int me;
    int nrProcs;
    MPI_Comm_size(MPI_COMM_WORLD, &nrProcs);
    MPI_Comm_rank(MPI_COMM_WORLD, &me);

    unsigned n;
    vector<int> v;
    if(argc != 2 || 1!=sscanf(argv[1], "%u", &n) ){
        fprintf(stderr, "usage: mergesort <n>\n");
        return 1;
    }


    if(me == 0) {
        generate(v, n);
        fprintf(stderr, "generated\n");
    }
    
    struct timespec beginTime;
    clock_gettime(CLOCK_REALTIME, &beginTime);
    
    if(me == 0) {
        mergeSort(v, nrProcs);
    } else {
        mergeWorker(n, me, nrProcs);
    }
    
    struct timespec endTime;
    clock_gettime(CLOCK_REALTIME, &endTime);
    int64_t const elapsedNs = (int64_t(endTime.tv_sec) - int64_t(beginTime.tv_sec))*1000000000LL
        + int64_t(endTime.tv_nsec) - int64_t(beginTime.tv_nsec);

    cout << "Elapsed time=" <<  (elapsedNs/1000000) <<"ms\n";
    if(me == 0) {
        cout << ((n==v.size() && isSorted(v)) ? "ok" : "WRONG") << "\n";
    }
    
    MPI_Finalize();
}