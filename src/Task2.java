import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Task2 extends Task {
    private int vertices;   // numărul persoanelor din rețea
    private int edges;      // numărul relațiilor de prietenie dintre ele
    private int k;
    private final LinkedHashMap<Integer, ArrayList<Integer>> relations
            = new LinkedHashMap<>();
    private String sol;
    private final List<Integer> solList = new ArrayList<>();

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        for (k = 1; k < vertices; k++) {
            formulateOracleQuestion();
            askOracle();
            decipherOracleAnswer();
            if (sol.equals("True")) {
                break;  // am găsit k pentru care se poate realiza o acoperire
            }
        }
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] stringArray = line.split("\\s+");
        vertices = Integer.parseInt(stringArray[0]);
        edges = Integer.parseInt(stringArray[1]);

        for (int i = 1; i <= vertices; i++) {
            relations.put(i, new ArrayList<>());
            // key - nodul, value - nodurile adiacente
        }

        for (int i = 0; i < edges; i++) {
            line = reader.readLine();
            stringArray = line.split("\\s+");
            ArrayList<Integer> list = relations.get(Integer.parseInt(stringArray[0]));
            list.add(Integer.parseInt(stringArray[1]));
        }
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("sat.cnf"));
        int variables = k * vertices;
        int clauses = k + vertices * k * (k - 1) / 2 + edges + k * vertices * (vertices - 1) / 2;
        writer.write("p cnf " + variables + " " + clauses + "\n");

        /*
        Primul tip de clauză
        Un nod trebuie să corespundă unei poziții din acoperire
        Număr clauze: k
        */
        for (int i = 0; i < k; i++) {
            for (int v = 1; v <= vertices; v++) {
                // Xiv % vertices <-> nodul din graf
                int Xiv = i * vertices + v;
                writer.write(Xiv + " ");
            }
            writer.write("0\n");
        }

        /*
        Al doilea tip de clauză
        Un nod nu poate fi reprezentat simultan de mai mulți indecși din acoperire
        Număr clauze: vertices * k * (k - 1) / 2
         */
        for (int v = 1; v <= vertices; v++) {
            for (int i = 0; i < k - 1; i++) {
                int Xiv = - (i * vertices + v);
                for (int j = i + 1; j < k; j++) {
                    int Xjv = - (j * vertices + v);
                    writer.write(Xiv + " " + Xjv + " 0\n");
                }
            }
        }

        /*
        Al treilea tip de clauză
        Două noduri adiacente nu pot avea aceeași culoare
        Număr de clauze: edges
         */
        for (int v = 1; v < vertices; v++) {
            ArrayList<Integer> list = relations.get(v);
            for (int w = v + 1; w <= vertices; w++) {
                if (list.contains(w)) {
                    for (int i = 0; i < k; i++) {
                        int Xiv = i * vertices + v;
                        int Xiw = i * vertices + w;
                        writer.write(Xiv + " " + Xiw + " ");
                    }
                    writer.write("0\n");
                }
            }
        }

        /*
        Al patrulea tip de clauză
        Un index nu poate reprezenta mai mult de un nod din acoperire
        Număr clauze: k * vertices * (vertices - 1) / 2
         */
        for (int i = 0; i < k; i++) {
            for (int v = 1; v < vertices; v++) {
                int Xiv = - (i * vertices + v);
                for (int w = v + 1; w <= vertices; w++) {
                    int Xiw = - (i * vertices + w);
                    writer.write(Xiv + " " + Xiw + " 0\n");
                }
            }
        }

        writer.flush();
        writer.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("sat.sol"));
        String line = reader.readLine();
        sol = line;

        if (sol.equals("True")) {
            line = reader.readLine();
            int variables = Integer.parseInt(line);
            line = reader.readLine();
            String[] stringArray = line.split("\\s+");

            for (int i = 0; i < variables; i++) {
                int variable = Integer.parseInt(stringArray[i]);
                if (variable > 0) {
                    solList.add(variable);
                }
            }
        }
    }

    @Override
    public void writeAnswer() throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

        if (sol.equals("True")) {
            for (int i = 0; i < solList.size(); i++) {
                int node = solList.get(i) % vertices;
                if (node == 0) {
                    // dacă restul este 0, node <-> ultimul nod din graf
                    writer.write(vertices + " ");
                } else {
                    writer.write(node + " ");
                }
            }
        }

        writer.flush();
        writer.close();
    }
}
