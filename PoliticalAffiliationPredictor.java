import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Instance;
import java.util.Arrays;

public class PoliticalAffiliationPredictor {

    private ArrayList<Map<String, String>> surveyData;
    private static final String[] parties = {"Republican", "Democrat", "Libertarian", "Green"};
    private Logistic model;
    private Instances dataset;

    // Initialize questions
    private static final String[][] questions = {
        {
            "What should the government do to help the poor?",
            "A. Make it easier to apply for assistance.",
            "B. Allow parents to use education funds for charter schools.",
            "C. Create welfare to work programs.",
            "D. Nothing."
        },
        {
            "What is your stance on environmental protection?",
            "A. Implement strict regulations to reduce carbon emissions.",
            "B. Incentivize businesses to adopt green practices.",
            "C. Focus on technological innovations to solve environmental issues.",
            "D. The market will naturally address environmental concerns."
        },
        {
            "What is your stance on healthcare?",
            "A. Healthcare should be universal and government-funded.",
            "B. Introduce market-based reforms to increase competition.",
            "C. Provide tax incentives for private health savings.",
            "D. Reduce government involvement in healthcare."
        },
        {
            "What is your stance on gun control?",
            "A. Implement stricter gun control laws.",
            "B. Enforce existing gun laws more effectively.",
            "C. Allow concealed carry nationwide.",
            "D. No regulations on guns."
        },
        {
            "What is your stance on immigration?",
            "A. Create a pathway to citizenship for undocumented immigrants.",
            "B. Strengthen border security.",
            "C. Implement a merit-based immigration system.",
            "D. Reduce overall immigration."
        },
        {
            "What is your stance on education?",
            "A. Increase funding for public schools.",
            "B. Support school choice and charter schools.",
            "C. Promote homeschooling and private education.",
            "D. Reduce federal involvement in education."
        },
        {
            "Which political party do you affiliate with?",
            "A. Republican",
            "B. Democrat",
            "C. Libertarian",
            "D. Green"
        }
    };

    public PoliticalAffiliationPredictor() {
        surveyData = new ArrayList<>();
        setupDataset();
    }

    private void setupDataset() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < questions.length - 1; i++) {
            attributes.add(new Attribute("Q" + (i + 1)));
        }
        ArrayList<String> classValues = new ArrayList<>(Arrays.asList(parties));
        attributes.add(new Attribute("Party", classValues));
        dataset = new Instances("SurveyData", attributes, 0);
        dataset.setClassIndex(dataset.numAttributes() - 1);
        model = new Logistic();
    }

    public void conductSurvey() {
        Scanner scanner = new Scanner(System.in);
        Map<String, String> responses = new HashMap<>();

        System.out.println("Welcome to the Political Affiliation Predictor Survey!");

        for (String[] questionSet : questions) {
            System.out.println(questionSet[0]); // Print the question
            for (int i = 1; i < questionSet.length; i++) {
                System.out.println(questionSet[i]); // Print the answer options
            }
            String answer;
            while (true) {
                System.out.print("Your answer: ");
                answer = scanner.nextLine().trim().toUpperCase();
                if (answer.matches("[A-D]")) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter A, B, C, or D.");
                }
            }
            responses.put(questionSet[0], answer); // Record response
        }

        surveyData.add(responses); // Add responses to data storage

        try {
            writeDataToFile(responses);
        } catch (IOException e) {
            System.out.println("An error occurred while writing data to files.");
            e.printStackTrace();
        }
    }

    public void trainModel() throws Exception {
        for (Map<String, String> response : surveyData) {
            double[] values = new double[dataset.numAttributes()];
            for (int i = 0; i < questions.length - 1; i++) {
                values[i] = answerToNumeric(response.get(questions[i][0]));
            }
            values[dataset.classIndex()] = Arrays.asList(parties).indexOf(response.get(questions[questions.length - 1][0]));
            dataset.add(new DenseInstance(1.0, values));
        }
        model.buildClassifier(dataset);
    }

    public void predictPoliticalAffiliation() throws Exception {
        Scanner scanner = new Scanner(System.in);
        double[] values = new double[questions.length - 1];

        System.out.println("Welcome to the Political Affiliation Predictor Prediction!");

        for (int i = 0; i < questions.length - 1; i++) {
            String[] questionSet = questions[i];
            System.out.println(questionSet[0]); // Print the question
            for (int j = 1; j < questionSet.length; j++) {
                System.out.println(questionSet[j]); // Print the answer options
            }
            String answer;
            while (true) {
                System.out.print("Your answer: ");
                answer = scanner.nextLine().trim().toUpperCase();
                if (answer.matches("[A-D]")) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter A, B, C, or D.");
                }
            }
            values[i] = answerToNumeric(answer);
        }

        Instance newInst = new DenseInstance(1.0, values);
        newInst.setDataset(dataset);
        double predictedClass = model.classifyInstance(newInst);
        System.out.println("Based on your responses, your predicted political affiliation is: " + parties[(int) predictedClass]);
    }

    private double answerToNumeric(String answer) {
        switch (answer) {
            case "A":
                return 0.0;
            case "B":
                return 1.0;
            case "C":
                return 2.0;
            case "D":
                return 3.0;
            default:
                return -1.0; // This should never happen due to input validation
        }
    }

    private void writeDataToFile(Map<String, String> responses) throws IOException {
        try (FileWriter writer = new FileWriter("survey_data.txt", true)) {
            for (Map.Entry<String, String> entry : responses.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
            writer.write("\n");
        }
    }

    public static void main(String[] args) {
        try {
            PoliticalAffiliationPredictor predictor = new PoliticalAffiliationPredictor();
            predictor.conductSurvey();
            predictor.trainModel();
            predictor.predictPoliticalAffiliation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
