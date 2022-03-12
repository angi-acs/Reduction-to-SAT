import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class Task3 extends Task {
    private int vertices;       // numărul de variabile
    private int edges;          // numărul relațiilor dintre ele
    private int colors;         // numărul de registre disponibile
    private final LinkedHashMap<Integer, ArrayList<Integer>> relations
            = new LinkedHashMap<>();
    private String sol;
    private final List<Integer> solList = new ArrayList<>();

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] stringArray = line.split("\\s+");
        vertices = Integer.parseInt(stringArray[0]);
        edges = Integer.parseInt(stringArray[1]);
        colors = Integer.parseInt(stringArray[2]);

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
        int variables = colors * vertices;
        int clauses = vertices + vertices * colors * (colors - 1) / 2 + edges * colors;
        writer.write("p cnf " + variables + " " + clauses + "\n");

        /*
        Primul tip de clauză
        Fiecare nod trebuie să aibă o culoare
        Număr clauze: vertices
         */
        for (int v = 1; v <= vertices; v++) {
            for (int i = 0; i < colors; i++) {
                int Xiv = i * vertices + v;
                writer.write(Xiv + " ");
            }
            writer.write("0\n");
        }

        /*
        Al doilea tip de clauză
        Un nod trebuie să aibă cel mult o culoare
        Număr clauze: vertices * colors * (colors - 1) / 2
         */
        for (int v = 1; v <= vertices; v++) {
            for (int i = 0; i < colors - 1; i++) {
                int Xiv = - (i * vertices + v);
                for (int j = i + 1; j < colors; j++) {
                    int Xjv = - (j * vertices + v);
                    writer.write(Xiv + " " + Xjv + " 0\n");
                }
            }
        }

        /*
        Al treilea tip de clauză
        Două noduri adiacente nu pot avea aceeași culoare
        Număr de clauze: edges * colors
         */
        for (int v = 1; v < vertices; v++) {
            ArrayList<Integer> list = relations.get(v);
            for (int w = v + 1; w <= vertices; w++) {
                if (list.contains(w)) {
                    for (int i = 0; i < colors; i++) {
                        int Xiv = - (i * vertices + v);
                        int Xiw = - (i * vertices + w);
                        writer.write(Xiv + " " + Xiw + " 0\n");
                    }
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

            /*
            Sortez lista astfel încât elementele să apară în ordinea nodurilor
            (înainte de sortare apar în ordinea culorilor)
             */
            solList.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    if (o1 % vertices == 0) {
                        return Integer.compare(vertices, o2 % vertices);
                    }
                    if (o2 % vertices == 0) {
                        return Integer.compare(o1 % vertices, vertices);
                    }
                    return Integer.compare(o1 % vertices, o2 % vertices);
                }
            });
        }
    }

    @Override
    public void writeAnswer() throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        writer.write(sol+"\n");

        if (sol.equals("True")) {
            for (int i = 0; i < solList.size(); i++) {
                int node = solList.get(i) / vertices; // numărul culorii asociate
                if (solList.get(i) % vertices != 0) {
                    node++;
                }
                writer.write(node + " ");
            }
        }

        writer.flush();
        writer.close();
    }
}
