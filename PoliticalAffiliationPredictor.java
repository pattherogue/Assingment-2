import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

public class PoliticalAffiliationPredictor {

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

    public static void main(String[] args) {
        PoliticalAffiliationPredictor predictor = new PoliticalAffiliationPredictor();
        predictor.conductSurveyAndPredict();
    }

    public void conductSurveyAndPredict() {
        Scanner scanner = new Scanner(System.in);
        int republicanScore = 0;
        int democratScore = 0;
        int libertarianScore = 0;
        int greenScore = 0;
        StringBuilder responses = new StringBuilder();
        
        System.out.println("Welcome to the Political Affiliation Predictor Survey!");

        // Collect responses for all questions
        for (String[] question : questions) {
            System.out.println(question[0]);
            for (int j = 1; j < question.length; j++) {
                System.out.println(question[j]);
            }
            String answer;
            while (true) {
                System.out.print("Your answer: ");
                answer = scanner.nextLine().trim().toUpperCase();
                if (answer.matches("[A-D]")) {
                    responses.append(answer).append(",");
                    switch (answer) {
                        case "A":
                            democratScore++;
                            break;
                        case "B":
                            republicanScore++;
                            break;
                        case "C":
                            greenScore++;
                            break;
                        case "D":
                            libertarianScore++;
                            break;
                    }
                    break;
                } else {
                    System.out.println("Invalid input. Please enter A, B, C, or D.");
                }
            }
        }
        
        // Determine the predicted party
        String predictedParty;
        if (republicanScore >= democratScore && republicanScore >= libertarianScore && republicanScore >= greenScore) {
            predictedParty = "Republican";
        } else if (democratScore >= republicanScore && democratScore >= libertarianScore && democratScore >= greenScore) {
            predictedParty = "Democrat";
        } else if (libertarianScore >= republicanScore && libertarianScore >= democratScore && libertarianScore >= greenScore) {
            predictedParty = "Libertarian";
        } else {
            predictedParty = "Green";
        }

        System.out.println("Based on your responses, your predicted political affiliation is: " + predictedParty);

        // Save user responses to a file
        saveUserResponse(predictedParty, responses.toString());

        // Close the scanner resource
        scanner.close();
    }

    private void saveUserResponse(String party, String responses) {
        String filename = party + "_responses.txt";
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write(responses + "\n");
        } catch (IOException e) {
            System.out.println("An error occurred while writing user responses to file.");
            e.printStackTrace();
        }
    }
}
