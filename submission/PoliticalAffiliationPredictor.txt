import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import java.io.File;
import java.util.Random;

public class PoliticalAffiliationPredictor {

    private Map<String, Map<String, Integer>> partyData;
    private static final String[] parties = {"Republican", "Democrat", "Libertarian", "Green"};
    private ArrayList<String[]> surveyResponses;

    private static final String[][] questions = {
        {"What should the government do to help the poor?", "A. Make it easier to apply for assistance.", "B. Allow parents to use education funds for charter schools.", "C. Create welfare to work programs.", "D. Nothing."},
        {"What is your stance on environmental protection?", "A. Implement strict regulations to reduce carbon emissions.", "B. Incentivize businesses to adopt green practices.", "C. Focus on technological innovations to solve environmental issues.", "D. The market will naturally address environmental concerns."},
        {"What is your stance on healthcare?", "A. Healthcare should be universal and government-funded.", "B. Introduce market-based reforms to increase competition.", "C. Provide tax incentives for private health savings.", "D. Reduce government involvement in healthcare."},
        {"What is your stance on gun control?", "A. Implement stricter gun control laws.", "B. Enforce existing gun laws more effectively.", "C. Allow concealed carry nationwide.", "D. No regulations on guns."},
        {"What is your stance on immigration?", "A. Create a pathway to citizenship for undocumented immigrants.", "B. Strengthen border security.", "C. Implement a merit-based immigration system.", "D. Reduce overall immigration."},
        {"What is your stance on education?", "A. Increase funding for public schools.", "B. Support school choice and charter schools.", "C. Promote homeschooling and private education.", "D. Reduce federal involvement in education."},
        {"Which political party do you affiliate with?", "A. Republican", "B. Democrat", "C. Libertarian", "D. Green"}
    };

    public PoliticalAffiliationPredictor() {
        partyData = new HashMap<>();
        for (String party : parties) {
            partyData.put(party, new HashMap<>());
        }
        surveyResponses = new ArrayList<>();
    }

    public void conductSurveyAndPredict() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Political Affiliation Predictor Survey!");

        String[] responses = new String[questions.length];
        for (int i = 0; i < questions.length - 1; i++) {
            System.out.println(questions[i][0]);
            for (int j = 1; j < questions[i].length; j++) {
                System.out.println(questions[i][j]);
            }
            String answer;
            while (true) {
                System.out.print("Your answer: ");
                answer = scanner.nextLine().trim().toUpperCase();
                if (answer.matches("[A-D]")) {
                    responses[i] = answer;
                    break;
                } else {
                    System.out.println("Invalid input. Please enter A, B, C, or D.");
                }
            }
            recordResponse(answer, questions[i][0]);
        }

        System.out.println(questions[questions.length - 1][0]);
        for (int j = 1; j < questions[questions.length - 1].length; j++) {
            System.out.println(questions[questions.length - 1][j]);
        }
        String actualParty;
        while (true) {
            System.out.print("Your answer: ");
            actualParty = scanner.nextLine().trim().toUpperCase();
            if (actualParty.matches("[A-D]")) {
                responses[questions.length - 1] = actualParty;
                break;
            } else {
                System.out.println("Invalid input. Please enter A, B, C, or D.");
            }
        }
        surveyResponses.add(responses);

        // Simulate additional survey responses
        simulateAdditionalResponses();

        try {
            writeDataToFile();
        } catch (IOException e) {
            System.out.println("An error occurred while writing data to files.");
            e.printStackTrace();
        }

        String predictedParty = predictParty(responses);
        System.out.println("Based on your responses, your predicted political affiliation is: " + predictedParty);
    }

    private void recordResponse(String answer, String question) {
        for (String party : parties) {
            Map<String, Integer> partyResponses = partyData.get(party);
            int weight = determineWeight(answer, party);
            partyResponses.put(question, partyResponses.getOrDefault(question, 0) + weight);
        }
    }

    private int determineWeight(String answer, String party) {
        switch (party) {
            case "Republican":
                return answer.equals("D") ? 3 : (answer.equals("B") ? 2 : 1);
            case "Democrat":
                return answer.equals("A") ? 3 : (answer.equals("C") ? 2 : 1);
            case "Libertarian":
                return answer.equals("C") ? 3 : (answer.equals("D") ? 2 : 1);
            case "Green":
                return answer.equals("A") ? 3 : (answer.equals("B") ? 2 : 1);
            default:
                return 1;
        }
    }

    private String predictParty(String[] responses) {
        try {
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File("survey_data.csv"));
            Instances data = loader.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);

            Classifier classifier = new J48();
            classifier.buildClassifier(data);

            ArrayList<Attribute> attributes = new ArrayList<>();
            for (int i = 0; i < questions.length - 1; i++) {
                attributes.add(new Attribute("Q" + (i + 1)));
            }
            ArrayList<String> classValues = new ArrayList<>();
            for (String party : parties) {
                classValues.add(party);
            }
            attributes.add(new Attribute("class", classValues));

            Instances newData = new Instances("TestInstances", attributes, 0);
            double[] instanceValues = new double[newData.numAttributes()];

            for (int i = 0; i < questions.length - 1; i++) {
                instanceValues[i] = responses[i].charAt(0) - 'A';
            }
            newData.add(new DenseInstance(1.0, instanceValues));
            newData.setClassIndex(newData.numAttributes() - 1);

            double predictedClass = classifier.classifyInstance(newData.firstInstance());
            return newData.classAttribute().value((int) predictedClass);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private void writeDataToFile() throws IOException {
        try (FileWriter writer = new FileWriter("survey_data.csv", true)) {
            for (String[] responses : surveyResponses) {
                writer.write(String.join(",", responses) + "\n");
            }
        }
    }

    private void simulateAdditionalResponses() {
        String[] possibleAnswers = {"A", "B", "C", "D"};
        Random random = new Random();
        for (int i = 0; i < 100; i++) { // Simulate 100 additional responses
            String[] simulatedResponse = new String[questions.length];
            for (int j = 0; j < questions.length; j++) {
                simulatedResponse[j] = possibleAnswers[random.nextInt(possibleAnswers.length)];
            }
            surveyResponses.add(simulatedResponse);
        }
    }

    public static void main(String[] args) {
        PoliticalAffiliationPredictor predictor = new PoliticalAffiliationPredictor();
        predictor.conductSurveyAndPredict();
    }
}
