import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.lang.Math;

public class SentenceGenerator35 {

    public static void main(String[] args){
        while(Menu.displayOperationPrompt());
    }

}

class Menu {
    private static final String[] operations1 = {
            "Random Sentence Generator",
            "Sorted Sentence Generator",
            "Ordered Sentence Generator",
            "Exit"
    };

    private static final String[] operations2 = {
            "Add a word to the vocabulary",
            "Generate a sentence",
            "Back"
    };

    public static boolean displayOperationPrompt() {
        for (int i = 1; i <= operations1.length; i++) {
            System.out.printf("%d. %s\n", i, operations1[i - 1]);
        }
        System.out.println("Please choose an option:");

        try {
            Scanner sc = new Scanner(System.in);
            int operationID = sc.nextInt();

            if(operationID == 4){
                return false;
            }

            if(operationID > 4 || operationID < 1) {
                System.out.println("Please enter a valid number\n");
                return true;
            }

            // Because operations is numbered from 1 to the user, but stored from 0
            executeOperation(operationID-1);

            return true;
        } catch (InputMismatchException exception){
            System.out.println("Please enter a valid number\n");
            return true;
        }
    }


     static boolean displayAddOrGeneratePrompt(GeneratorStrategy generatorStrategy){
        for (int i = 1; i <= operations2.length; i++) {
         System.out.printf("%d. %s\n", i, operations2[i - 1]);
        }

        try {
            Scanner sc = new Scanner(System.in);
            int operationID = sc.nextInt();

            if (operationID == 1) {
                System.out.println("Please enter a word to be add to the vocabulary:");
                sc.nextLine();
                String word = sc.nextLine();
                generatorStrategy.addToVocabulary(word);
            } else if (operationID == 2) {
                String sentence = generatorStrategy.generate();
                System.out.println(sentence);
            } else {
                return false;
            }

            return true;
        } catch (InputMismatchException exception){
            System.out.println("Please enter a valid number\n");
            return true;
        }
    }


    private static void executeOperation(int operationID) {
        System.out.println("-------------------------------------");
        System.out.println(operations1[operationID] + " Selected\n");

        StringGenerator stringGenerator = new StringGenerator();
        GeneratorStrategy generatorStrategy;

        if(operationID == 0){
            // Random sentence generator requested
            generatorStrategy = new RandomSentenceGenerator();
            stringGenerator.setGeneratorStrategy(generatorStrategy);
        }
        else if(operationID == 1){
            // Sorted sentence generator requested
            generatorStrategy = new SortedSentenceGenerator();
            stringGenerator.setGeneratorStrategy(generatorStrategy);
        }
        else{
            // Ordered sentence generator requested
            generatorStrategy = new OrderedSentenceGenerator();
            stringGenerator.setGeneratorStrategy(generatorStrategy);
        }

        while(displayAddOrGeneratePrompt(generatorStrategy));
    }
}


class StringGenerator{
    private GeneratorStrategy generatorStrategy;

    public void setGeneratorStrategy(GeneratorStrategy generatorStrategy){
        this.generatorStrategy = generatorStrategy;
    }

    public String generateSentence(){
        return generatorStrategy.generate();
    }
}

interface GeneratorStrategy{
    String generate();
    ArrayList<String> prepareWords();
    String concatenate(ArrayList<String> words);
    void addToVocabulary(String word);
    String setCaseAndFormat(String word);
}

class RandomSentenceGenerator implements GeneratorStrategy{
    private static ArrayList<String> vocabulary = new ArrayList<>();

    @Override
    public String generate() {
        try {
            ArrayList<String> words = prepareWords();
            String generatedSentence = concatenate(words);

            return generatedSentence;
        } catch (NullPointerException exception){
            return "";
        }
    }

    @Override
    public ArrayList<String> prepareWords()
    {
        ArrayList<String> vocabulary = getVocabulary();

        // We want the the number range to be [1, total number of words] not [0, total number of words - 1]
        int numberOfWords = (int) ((Math.random()*100000000)%vocabulary.size()) + 1;
        ArrayList<String> words = new ArrayList<>(numberOfWords);

        try {
            for (int i = 0; i < numberOfWords; i++) {
                int randomIndex = (int) ((Math.random() * 100000000) % numberOfWords);
                words.add(vocabulary.get(randomIndex));
            }

            return words;
        } catch (IndexOutOfBoundsException exception){
            if(vocabulary.isEmpty())
                System.out.println("Vocabulary is empty. Please try adding some words first.");
            else
                System.out.println("Exception Occurred: Vocabulary index out of bound.");

            return null;
        }
    }

    @Override
    public String concatenate(ArrayList<String> words) {
        try {
            String sentence = words.get(0);
            for (int i = 1; i < words.size(); i++) {
                sentence = sentence.concat(" " + words.get(i));
            }

            return sentence;
        } catch (IndexOutOfBoundsException exception){
            System.out.println("Exception occurred: The word list array is empty!");
            return null;
        }
    }

    @Override
    public void addToVocabulary(String word) {
        // Convert the word into lower case
        word = setCaseAndFormat(word);

        vocabulary = getVocabulary();
        vocabulary.add(word);
    }

    @Override
    public String setCaseAndFormat(String word) {
        return word.toLowerCase();
    }

    // Special getter method is needed since static field needs a static method to be accessed
    public static ArrayList<String> getVocabulary() {
        return vocabulary;
    }
}

class SortedSentenceGenerator implements GeneratorStrategy{
    private static ArrayList<String> vocabulary = new ArrayList<>();

    @Override
    public String generate() {
        try {
            ArrayList<String> words = prepareWords();
            String generatedSentence = concatenate(words);

            return generatedSentence;
        } catch (NullPointerException exception){
            return "";
        }
    }

    @Override
    public ArrayList<String> prepareWords() {
        ArrayList<String> vocabulary = getVocabulary();

        // We want the the number range to be [1, total number of words] not [0, total number of words - 1]
        // Max number words can be of 1.5x length of the vocabulary array
        int numberOfWords = (int) ((Math.random()*100000000)%(vocabulary.size() + vocabulary.size()/2) ) + 1;
        ArrayList<String> words = new ArrayList<>(numberOfWords);

        try {
            for (int i = 0; i < numberOfWords; i++) {
                int randomIndex = (int) ((Math.random() * 100000000) % numberOfWords);
                words.add(vocabulary.get(randomIndex));
            }

            // Sort the words
            Collections.sort(words);

            return words;
        } catch (IndexOutOfBoundsException exception){
            if(vocabulary.isEmpty())
                System.out.println("Vocabulary is empty. Please try adding some words first.");
            else
                System.out.println("Exception Occurred: Vocabulary index out of bound.");

            return null;
        }
    }

    @Override
    public String concatenate(ArrayList<String> words) {
        try {
            String sentence = words.get(0);
            for (int i = 1; i < words.size(); i++) {
                sentence = sentence.concat(" " + words.get(i));
            }

            return sentence;
        } catch (IndexOutOfBoundsException exception){
            System.out.println("Exception occurred: The word list array is empty!");
            return null;
        }
    }

    @Override
    public void addToVocabulary(String word) {
        // Convert the word into lower case
        word = setCaseAndFormat(word);

        vocabulary = getVocabulary();
        vocabulary.add(word);
    }

    @Override
    public String setCaseAndFormat(String word) {
        return word.toLowerCase();
    }

    // Special getter method is needed since static field needs a static method to be accessed
    public static ArrayList<String> getVocabulary() {
        return vocabulary;
    }
}

class OrderedSentenceGenerator implements GeneratorStrategy{
    private static ArrayList<String> vocabulary = new ArrayList<>();

    @Override
    public String generate() {
        try {
            ArrayList<String> words = prepareWords();
            String generatedSentence = concatenate(words);

            return generatedSentence;
        } catch (NullPointerException exception){
            return "";
        }
    }

    @Override
    public ArrayList<String> prepareWords() {
        ArrayList<String> vocabulary = getVocabulary();

        if(vocabulary.isEmpty()){
            System.out.println("Vocabulary is empty. Please try adding some words first.");

            return null;
        }
        return vocabulary;
    }

    @Override
    public String concatenate(ArrayList<String> words) {
        try {
            String sentence = words.get(0);
            for (int i = 1; i < words.size(); i++) {
                sentence = sentence.concat(" " + words.get(i));
            }

            return sentence;
        } catch (IndexOutOfBoundsException exception){
            System.out.println("Exception occurred: The word list array is empty!");
            return null;
        }
    }

    @Override
    public void addToVocabulary(String word) {
        // Convert the word into lower case
        word = setCaseAndFormat(word);

        vocabulary = getVocabulary();
        vocabulary.add(word);
    }

    @Override
    public String setCaseAndFormat(String word) {
        word = word.toUpperCase();
        String reverseWord = "";
        for(int i=0; i<word.length(); i++){
            reverseWord = reverseWord.concat(String.valueOf(word.charAt(i)));
        }
        return reverseWord;
    }

    // Special getter method is needed since static field needs a static method to be accessed
    public static ArrayList<String> getVocabulary() {
        return vocabulary;
    }
}