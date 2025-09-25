import java.io.*;
import java.util.*;

public class TaskPlanning {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("schedule.in"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("schedule.out"));
        int n = Integer.parseInt(reader.readLine().trim());
        Task[] tasks = new Task[n];

        for (int i = 0; i < n; i++) {
            String[] line = reader.readLine().split(" ");
            int d = Integer.parseInt(line[0]);
            int w = Integer.parseInt(line[1]);
            tasks[i] = new Task(d, w);
        }
        reader.close();

        long result = minimize(tasks, n);
        writer.write(Long.toString(result));
        writer.newLine();
        writer.close();
    }

    private static long minimize(Task[] tasks, int n) {
        Comparator<Task> taskComparator = (a, b) -> {
            if (b.w != a.w) {
                return Integer.compare(b.w, a.w);
            }
            return Integer.compare(a.d, b.d); 
        };

        Arrays.sort(tasks, taskComparator);


        TreeSet<Integer> availableSlots = new TreeSet<>();
        for (int i = 1; i <= n; i++) {
            availableSlots.add(i);
        }

        long total = 0;

        for (Task task : tasks) {
            Integer slot = availableSlots.floor(task.d);
            if (slot != null) {
                availableSlots.remove(slot); 
            } else {
                total += task.w;
            }
        }

        return total;
    }

    static class Task {
        int d;
        int w; 

        Task(int d, int w) {
            this.d = d;
            this.w = w;
        }
    }
}

