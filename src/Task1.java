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

public class Task1 extends Task {
    private int vertices;   // numărul membrilor rețelei de socializare
    private int edges;      // numărul relațiilor de prietenie dintre aceștia
    private int clique;     // dimensiunea grupului de persoane căutat
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
        clique = Integer.parseInt(stringArray[2]);

        for (int i = 1; i <= vertices; i++) {
            relations.put(i, new ArrayList<>());
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
        int variables = clique * vertices;
        int clauses = clique +
                (vertices * (vertices - 1) / 2 - edges) * clique * (clique - 1) +
                vertices * clique * (clique - 1) / 2 +
                clique * vertices * (vertices - 1) / 2;
        writer.write("p cnf " + variables + " " + clauses + "\n");

        /*
        Primul tip de clauză
        Un nod trebuie să corespundă unei poziții din clică
        Număr clauze: clique
         */
        for (int i = 0; i < clique; i++) {
            for (int v = 1; v <= vertices; v++) {
                int Xiv = i * vertices + v;
                writer.write(Xiv + " ");
            }
            writer.write("0\n");
        }

        /*
        Al doilea tip de clauză
        Pentru fiecare non-muchie (v, w), nodurile v și w nu pot fi ambele în
        clică deoarece trebuie să existe o muchie între oricare două noduri ca
        să poată fi formată o clică
        Număr clauze: (vertices * (vertices - 1) / 2 - edges) * clique * (clique - 1)
        În paranteză calculez numărul maxim de muchii posibile în graf, din care
        scad numărul de muchii existente => numărul de non-muchii, adică de câte
        ori se intră pe primul if
         */
        for (int v = 1; v < vertices; v++) {
            ArrayList<Integer> list = relations.get(v);
            for (int w = v + 1; w <= vertices; w++) {
                if (!list.contains(w)) {
                    for (int i = 0; i < clique; i++) {
                        int Xiv = - (i * vertices + v);
                        for (int j = 0; j < clique; j++) {
                            if (i != j) {
                                int Xjw = - (j * vertices + w);
                                writer.write(Xiv + " " + Xjw + " 0\n");
                            }
                        }
                    }
                }
            }
        }

        /*
        Al treilea tip de clauză
        Un nod nu poate fi reprezentat simultan de mai mulți indecși din clică
        Număr clauze: vertices * clique * (clique - 1) / 2
         */
        for (int v = 1; v <= vertices; v++) {
            for (int i = 0; i < clique - 1; i++) {
                int Xiv = - (i * vertices + v);
                for (int j = i + 1; j < clique; j++) {
                    int Xjv = - (j * vertices + v);
                    writer.write(Xiv + " " + Xjv + " 0\n");
                }
            }
        }

        /*
        Al patrulea tip de clauză
        Un index nu poate reprezenta mai mult de un nod în clică
        Număr clauze: clique * vertices * (vertices - 1) / 2
         */
        for (int i = 0; i < clique; i++) {
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
        writer.write(sol+"\n");

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
