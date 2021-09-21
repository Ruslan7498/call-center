import java.util.Queue;
import java.util.concurrent.*;

class CallCenter {
    private Queue<String> queue = null;
    private volatile boolean cycle = true;
    public static final int NUMBER_OF_SPESIALST = 5;
    public static final int TIME_OF_SPESIALST = 500;
    public static final int NUMBER_OF_CALLS = 100;
    public static final int CALLS_IN_SECOND = 5;
    public static final int SECOND = 1000;
    public static final int TIME_WORK_SPESIALST = 3000;

    CallCenter() {
        queue = new ConcurrentLinkedQueue<String>(); // неблокирующая очередь
        //queue = new ArrayBlockingQueue<String>(NUMBER_OF_CALLS, true); // фиксированый размер очереди
        //queue = new PriorityBlockingQueue<String>(); // нечестная блокирующая очередь
        //queue = new LinkedBlockingDeque<String>(); // блокирующая очередь
        //queue = new LinkedTransferQueue<String>(); // блокирующая очередь
        createThreadAST();
        Thread threadAST = new Thread(new ThreadAST());
        threadAST.start();
    }

    public void createThreadAST() {
        for (int i = 1; i <= NUMBER_OF_SPESIALST; i++) {
            Thread specialist = new Thread(new ThreadSpecialist());
            String name = "Специалист номер " + i;
            specialist.setName(name);
            specialist.start();
            try {
                Thread.sleep(TIME_OF_SPESIALST);
            } catch (InterruptedException e) {
                e.getMessage();
            }
        }
    }

    class ThreadAST implements Runnable {
        @Override
        public void run() {
            try {
                for (int i = 1; i <= NUMBER_OF_CALLS; i++) {
                    String str = "звонок номер: " + i;
                    queue.add(str);
                    cycle = false;
                    System.out.println("Ожидает на линии " + str);
                    if ((i % CALLS_IN_SECOND) == 0) Thread.sleep(SECOND);
                }
            } catch (InterruptedException e) {
                e.getMessage();
            }
        }
    }

    class ThreadSpecialist implements Runnable {
        @Override
        public void run() {
            String str;
            while (cycle || !queue.isEmpty()) {
                str = queue.poll();
                if (str != null) {
                    System.out.println(Thread.currentThread().getName() + " обрабатывает " + str);
                } else {
                    System.out.println("Нет звонков в ожидании");
                }
                try {
                    Thread.sleep(TIME_WORK_SPESIALST);
                } catch (InterruptedException e) {
                    e.getMessage();
                }
            }
        }
    }
}
