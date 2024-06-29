public class MMBurgers implements MMBurgersInterface {

    //creating various parameters that will be used in simulating the entire event

    int maxburgers; 
    int numBillingQueues;
    Queue[] BillingQueues; 
    int clock = 0;
    int clockEnd;
    Queue griddle =  new Queue();
    Queue griddleWaiting = new Queue();
    customer[] customerlist = new customer[1000];
    int numcustomers;

    public boolean isEmpty(){
        //checks whether any more events are left to be simulated
        //gives true when all customers have received their orders

        int ans = 1;
        for (int i=0; i<numcustomers; i++){
            int x = customerlist[i].orderfinishedtime + 1 ;
            if (clockEnd<x){
                ans = 0;
                break;
            }
        }
        if (ans == 1){
            return true;
        }else{
            return false;
        }
    } 
    
    public void setK(int k) throws IllegalNumberException{
        //sets the value of number of billing queues present in the restaurant
        //forms an array of queues
        //this array (BillingQueues) contains all the billing queues as its elements

        if(k<0){
            throw new IllegalNumberException("");
        }
        numBillingQueues = k;
        BillingQueues = new Queue[k];
        int i=0;
        while (i<k){
            Queue q = new Queue();
            BillingQueues[i] = q;
            q.qnum = i+1;
            i+=1;
        }   
    }   
    
    public void setM(int m) throws IllegalNumberException{
        //sets the value of maximum number of burgers that can be cooked 
        //simultaneously on the griddle

        if(m<0){
            throw new IllegalNumberException("");
        }
        maxburgers = m;
    } 

    public void advanceTime(int t) throws IllegalNumberException{
        //sets the value of time upto which simulations have to be carried out

        if(t<0){
            throw new IllegalNumberException("");
        }
        clockEnd = t;
        simulateevents(clockEnd);
    } 

    public void arrival(customer c){
        //simulates the arrival of a customer by putting it into the assigned billing queue
        //it also find the time when order is placed by taking into account
        //the number of people present in the queue before him and the efficiency of the billing counter

        c.qassigned.enqueue(c.id);
        c.orderplacedtime = c.arrivaltime + (c.qassigned.rear+1)*(c.qassigned.qnum);
    }

    public void simulateevents(int time){
        //simulates all the events happening for each customer
        //creates a global clock
        //compares the clock with values like orderplacedtime, orderstartedtime, orderfinishedtime
        //and decides what the next event should be
        //checks the state of griddle and waiting line for griddle 
        //and prepares the orders on each customer accordingly

        while ((clock>=0)&&(clock<=time)){
            for (int p=0; p<numcustomers; p++){
                customer c = customerlist[p];
                if(c.qassigned == null && clock == c.orderplacedtime){
                    if (griddle.rear+1 < maxburgers){
                        int space = maxburgers - griddle.rear - 1;
                        if (c.numburgers<space){
                            space = c.numburgers;
                        }
                        for (int i = 0; i<space; i++){
                            griddle.enqueue(c.id);
                            griddleWaiting.dequeue();
                        }
                        c.orderstartedtime = clock;
                        c.numburgers -= space;
                    }
                }else if (clock == c.orderplacedtime){
                    c.qassigned.dequeue();
                    c.qassigned = null;
                    if (griddle.rear+1 == maxburgers){
                        for (int i=0; i<c.numburgers; i++){
                            griddleWaiting.enqueue(c.id);
                        }
                    }else if (griddle.rear+1 < maxburgers){
                        int space = maxburgers - griddle.rear - 1;
                        if (c.numburgers<space){
                            space = c.numburgers;
                        }
                        for (int i = 0; i<space; i++){
                            griddle.enqueue(c.id);
                        }
                        c.orderstartedtime = clock;
                        c.numburgers -= space;
                        if(c.numburgers>0){
                            for (int j = 0; j<c.numburgers; j++){
                                griddleWaiting.enqueue(c.id);
                            }
                        }
                    }

                }else if ((clock > c.orderplacedtime)&&(clock<c.orderfinishedtime)){
                    if ((c.numburgers==0)&&(clock==c.orderplacedtime+10)){
                        c.orderfinishedtime = clock;
                        while (griddle.front == c.id){
                            griddle.dequeue();
                        }
                        c.orderplacedtime = c.orderstartedtime;
                    }else if ((c.numburgers!=0)&&(clock == c.orderplacedtime+10)){
                        int x = c.burgers - c.numburgers;
                        for (int i=0; i<x; i++){
                            griddle.dequeue();
                        }
                        c.orderplacedtime = c.orderstartedtime;
                        int y = 0;
                        for(int i = 0; i < griddle.rear+1; i++){
                            if(griddle.qu[i]==c.id){
                                y++;
                            }
                        }
                        c.numburgers -= y;
                        c.orderplacedtime = c.orderstartedtime;
                        int space = maxburgers - griddle.rear - 1;
                        if (c.numburgers<space){
                            space = c.numburgers;
                        }
                        if(space>0){
                            for (int j = 0; j<space; j++){
                                griddle.enqueue(c.id);
                                griddleWaiting.dequeue();
                            }
                        }
                        c.numburgers -= space;
                    }else if ((c.numburgers!=0)&&(clock != c.orderplacedtime+10)){
                        int space = maxburgers - griddle.rear - 1;
                        if (c.numburgers<space){
                            space = c.numburgers;
                        }
                        if(space>0){
                            for (int j = 0; j<space; j++){
                                griddle.enqueue(c.id);
                                griddleWaiting.dequeue();
                            }
                            c.orderstartedtime = clock;
                        }
                    }
                }
                if(c.orderstartedtime==0 && clock >= c.orderplacedtime){
                    c.orderplacedtime++;
                }
            }
            clock+=1;
        }
    }

    public void arriveCustomer(int id, int t, int numb) throws IllegalNumberException{
        //simulates arrival of a customer
        //assigns all the parameters to the customer
        //puts into the customer list
        //calls the function arrival(c) for further simulation of events

        customer c = new customer();
        c.id = id;
        c.numburgers = numb;
        c.burgers = numb;
        c.arrivaltime = t;

        Queue minq = BillingQueues[0];
        for(int i=1; i<numBillingQueues; i++){
            if (BillingQueues[i].rear+1<minq.rear+1){
                minq = BillingQueues[i];
            }
        }
        c.qassigned = minq;

        customerlist[id-1] = c;
        numcustomers += 1;
        arrival(c);

    } 

    public int customerState(int id, int t) throws IllegalNumberException{
        //gives the state of customer at time t

        simulateevents(t);
        customer c = customerlist[id-1];
        if (c==null){
            throw new IllegalNumberException("");
        }
        if(t<c.arrivaltime){
            return 0;
        }else if((t>=c.arrivaltime)&&(t<c.orderplacedtime)){
            return c.qassigned.qnum;
        }else if((t>=c.orderplacedtime)&&(t<c.orderfinishedtime)){
            return numBillingQueues + 1;
        }else{
            return numBillingQueues + 2;
        }
    } 

    public int griddleState(int t) throws IllegalNumberException{
        //gives the state of griddle (how many burgers are cooking) at time t

        simulateevents(t);
        return griddle.rear+1;
    } 

    public int griddleWait(int t) throws IllegalNumberException{
        //gives the state of waiting list for griddle (how many burgers are waiting) at time t

        simulateevents(t);
        return griddleWaiting.rear+1;
    } 

    public int customerWaitTime(int id) throws IllegalNumberException{
        //gives the total wait time of a customer

        customer c = customerlist[id-1];
        return c.orderfinishedtime - c.arrivaltime + 1;
    } 

	public float avgWaitTime(){
        //gives the average wait time of all the customers

        int sum=0;
        for (int i=0; i<numcustomers; i++){
            customer c = customerlist[i];
            sum += c.orderfinishedtime - c.arrivaltime + 1;
        }
        float sum1 = (float)sum;
        return sum1/numcustomers;
    } 

    
}