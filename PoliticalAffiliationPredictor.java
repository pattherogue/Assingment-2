import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;

public class PoliticalAffiliationPredictor {

    private Map<String, Map<String, Integer>> partyData;
    private static final String[] parties = {"Republican", "Democrat", "Libertarian", "Green"};
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
            "Which political party do you affiliate with?",
            "A. Republican",
            "B. Democrat",
            "C. Libertarian",
            "D. Green"
        }
    };

    public PoliticalAffiliationPredictor() {
        partyData = new HashMap<>();
        for (String party : parties) {
            partyData.put(party, new HashMap<>());
        }
    }

    public void conductSurveyAndPredict() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Political Affiliation Predictor Survey!");
        
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
                break;
            } else {
                System.out.println("Invalid input. Please enter A, B, C, or D.");
            }
        }

        String predictedParty = predictParty();
        System.out.println("Based on your responses, your predicted political affiliation is: " + predictedParty);
        saveUserResponse(actualParty);

        try {
            writeDataToFile(partyData);
        } catch (IOException e) {
            System.out.println("An error occurred while writing data to files.");
            e.printStackTrace();
        }
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

    private String predictParty() {
        String predictedParty = "";
        int maxScore = 0;
        for (String party : parties) {
            int partyScore = calculatePartyScore(party);
            if (partyScore > maxScore) {
                maxScore = partyScore;
                predictedParty = party;
            }
        }
        return predictedParty;
    }

    private int calculatePartyScore(String party) {
        int score = 0;
        Map<String, Integer> partyResponses = partyData.get(party);
        for (int value : partyResponses.values()) {
            score += value;
        }
        return score;
    }

    private void saveUserResponse(String actualParty) {
        try (FileWriter writer = new FileWriter("user_responses.csv", true)) {
            for (String[] questionSet : questions) {
                String answer = questionSet[0];
                writer.write(answer + ",");
            }
            writer.write(actualParty + "\n");
        } catch (IOException e) {
            System.out.println("An error occurred while writing user responses to file.");
            e.printStackTrace();
        }
    }

    private static void writeDataToFile(Map<String, Map<String, Integer>> partyData) throws IOException {
        for (String party : parties) {
            Map<String, Integer> partyResponses = partyData.get(party);
            try (FileWriter writer = new FileWriter(party + "_data.csv")) {
                for (Map.Entry<String, Integer> entry : partyResponses.entrySet()) {
                    writer.write(entry.getKey() + "," + entry.getValue() + "\n");
                }
            }
        }
    }

    public static void main(String[] args) {
        PoliticalAffiliationPredictor predictor = new PoliticalAffiliationPredictor();
        predictor.conductSurveyAndPredict();
    }
}
