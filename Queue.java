public class Queue {
    //this class creates a queue using growable arrays
    //takes care of basic operations in a queue


    int[] qu = new int[1]; 
    int size = 1;
    int front;
    int rear = -1;
    int qnum;

    public int QueueNum(){
        return qnum;
    }

    public boolean isEmpty(){
        if (rear==-1){
            return true;
        }else{
            return false;
        }
    } 


    public int queueSize(){
        return rear+1;
    }


    public void enqueue(int i){
        if (rear!=size-1){
            qu[rear+1] = i;
            rear+=1;
            front = qu[0];
        }else{
            int[] newqu = new int[2*size];
            for (int j=0; j<size; j++){
                newqu[j]=qu[j];
            }
            size*=2;
            qu=newqu;
            qu[rear+1] = i;
            rear+=1;
            front = qu[0];
        }
    }
    
    
    public void dequeue(){
        if(rear!=0){
            for (int i=0; i<rear; i++){
                qu[i]=qu[i+1];
            }
        }
        qu[rear] = 0;
        rear -= 1;
        front = qu[0];
    } 

}