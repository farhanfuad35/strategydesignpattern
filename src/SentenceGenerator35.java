import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.lang.Math;

public class SentenceGenerator35 {

    public static void main(String[] args){
        while(Menu.chooseStringGenerator());
    }

}

class Menu {
    private static final String[] generators = {
            "Random Sentence Generator",
            "Sorted Sentence Generator",
            "Ordered Sentence Generator",
            "Exit"
    };

    private static final String[] operations = {
            "Add a word to the vocabulary",
            "Generate a sentence",
            "Back"
    };

    // This method displays a prompt to the users and let them select the desired generator
    // Return true if "choose generator" prompt should be displayed again
    // Return false to exit the program
    public static boolean chooseStringGenerator() {

        // Display the list of string generators
        for (int i = 1; i <= generators.length; i++) {
            System.out.printf("%d. %s\n", i, generators[i - 1]);
        }
        System.out.println("Please choose an option:");


        try {
            // Takes user input
            Scanner sc = new Scanner(System.in);
            int operationID = sc.nextInt();

            // User wants to exit
            if(operationID == 4){
                return false;
            }

            // Wrong operation number inserted
            if(operationID > 4 || operationID < 1) {
                System.out.println("Please enter a valid number\n");
                return true;
            }

            // Proper operation has been selected
            System.out.println("-------------------------------------");
            // Original array index and user input differ by 1 since arrays start from 0
            System.out.println(generators[operationID-1] + " Selected\n");

            // All string generation operations are executed through StringGenerator class by the client
            StringGenerator stringGenerator = new StringGenerator();

            // Random sentence generator requested
            if(operationID == 1){
                GeneratorStrategy generatorStrategy = new RandomSentenceGenerator();
                stringGenerator.setGeneratorStrategy(generatorStrategy);
            }
            // Sorted sentence generator requested
            else if(operationID == 2){
                GeneratorStrategy generatorStrategy = new SortedSentenceGenerator();
                stringGenerator.setGeneratorStrategy(generatorStrategy);
            }
            // Ordered sentence generator requested
            else{
                GeneratorStrategy generatorStrategy = new OrderedSentenceGenerator();
                stringGenerator.setGeneratorStrategy(generatorStrategy);
            }

            // Keep showing the "Add or Generate" until user wants to go back to the previous menu
            while(chooseAddOrGenerate(stringGenerator));

            // Return true to show the Generator selection prompt again
            return true;
        }
        // Handles exception in case user enters a String instead of an integer at the "choose generator" prompt
        catch (InputMismatchException exception){
            System.out.println("Please enter a valid number\n");
            return true;
        }
    }

    // This method lets the users choose what to do with the generator: 1) Add word to the vocabulary
    // or 2) Generate a new sentence
    // Return true if the user wants to try the generator again
    // Return false if the user wants to go back to the previous menu
    static boolean chooseAddOrGenerate(StringGenerator stringGenerator){
        // Display the selection prompt
        for (int i = 1; i <= operations.length; i++) {
            System.out.printf("%d. %s\n", i, operations[i - 1]);
        }

        // There is an user input method here which can throw InputMismatchException in case the user input
        // is not an integer as expected
        try {
            // Takes user input
            Scanner sc = new Scanner(System.in);
            int operationID = sc.nextInt();

            // User wants to add a word to the vocabulary
            if (operationID == 1) {
                System.out.println("Please enter a word to be add to the vocabulary:");
                sc.nextLine();
                String word = sc.nextLine();
                stringGenerator.executeVocabularyAddition(word);
            }
            // User wants to generate a sentence
            else if (operationID == 2) {
                String sentence = stringGenerator.executeStringGeneration();
                System.out.println(sentence);
            }
            // User wants to go back to the previous menu
            else if(operationID == 3){
                return false;
            }
            // User inserted an invalid operation number
            else{
                System.out.println("Please enter a valid number.\n");
                return true;
            }

            // Show the "Add or Generate String" prompt again
            return true;
        } catch (InputMismatchException exception){
            System.out.println("Please enter a valid number.\n");
            return true;
        }
    }
}

// This class behaves as the context class of the Strategy Design Pattern
// It is responsible for executing the strategy on the client's request and
// is not really aware of all the strategies available (RSG, SSG, OSG). Rather
// the class operates on a common interface for all the strategies - Generator Strategy.
class StringGenerator{
    // To hold the strategy set by the client
    GeneratorStrategy generatorStrategy;

    // Method to let the client set the strategy
    public void setGeneratorStrategy(GeneratorStrategy generatorStrategy){
        this.generatorStrategy = generatorStrategy;
    }

    // This method is responsible for actually generating the sentence using the interface
    // and also printing them
    public String executeStringGeneration(){
       try {
           ArrayList<String> words = generatorStrategy.prepareWords();
           String generatedSentence = generatorStrategy.concatenate(words);

           if(words == null || generatedSentence == null)
               throw new NullPointerException();

           return generatedSentence;
       } catch (NullPointerException exception){
           return "";
       }
    }

    // This method is responsible for adding a word to the vocabulary of the generator set previously
    public void executeVocabularyAddition(String word){
       String formattedWord = generatorStrategy.setCaseAndFormat(word);
       generatorStrategy.addToVocabulary(formattedWord);
    }
}

// Interface for all the different string generator strategies available.
// Interface allows StringGenerator class to work independently without the need to
// know what type of generator actually is being executed. This makes it easier to add
// new generator in future and requires no additional change in the StringGenerator class
interface GeneratorStrategy{

    // To prepare a list of words from the vocabulary as needed by a specific generator algorithm
    ArrayList<String> prepareWords();

    // Concatenates the words generated by the prepareWords() method
    String concatenate(ArrayList<String> words);

    // Sets the case and format before saving to the vocabulary as needed by a particular algorithm
    String setCaseAndFormat(String word);

    // Add the formatted word word to the vocabulary
    void addToVocabulary(String formattedWord);

}

class RandomSentenceGenerator implements GeneratorStrategy{

    // To hold the vocabulary for this generator
    private static ArrayList<String> vocabulary = new ArrayList<>();

    // To prepare a list of words from the vocabulary as needed by a specific generator algorithm
    @Override
    public ArrayList<String> prepareWords()
    {
        ArrayList<String> vocabulary = getVocabulary();

        // We want the the number range to be [1, total number of words] not [0, total number of words - 1]
        // Max number words can be of 1.5x length of the vocabulary array
        int numberOfWords = (int) ((Math.random()*100000000)%(vocabulary.size() + vocabulary.size()/2) ) + 1;

        ArrayList<String> words = new ArrayList<>(numberOfWords);

        try {
            for (int i = 0; i < numberOfWords; i++) {
                int randomIndex = (int) ((Math.random() * 100000000) % vocabulary.size());
                words.add(vocabulary.get(randomIndex));
            }

            return words;
        } catch (IndexOutOfBoundsException exception){
            if(vocabulary.isEmpty())
                System.out.println("Vocabulary is empty. Please try adding some words first. ");
            else
                System.out.println("Exception Occurred: Vocabulary index out of bound." + exception.getLocalizedMessage());

            return null;
        }
    }

    // Concatenates the words generated by the prepareWords() method
    @Override
    public String concatenate(ArrayList<String> words) {
        try {
            String sentence = words.get(0);
            for (int i = 1; i < words.size(); i++) {
                sentence = sentence.concat(" " + words.get(i));
            }

            return sentence;
        } catch (IndexOutOfBoundsException exception){
            System.out.println("Exception occurred: The word list array is empty! ");
            return null;
        }
    }

    @Override
    public String setCaseAndFormat(String word) {
        // Convert the word into lower case
        return word.toLowerCase();
    }

    @Override
    public void addToVocabulary(String formattedWord) {
        vocabulary = getVocabulary();
        vocabulary.add(formattedWord);
    }

    // Special getter method is needed since static field needs a static method to be accessed
    public static ArrayList<String> getVocabulary() {
        return vocabulary;
    }
}

class SortedSentenceGenerator implements GeneratorStrategy{

    // To hold the vocabulary for this generator
    private static ArrayList<String> vocabulary = new ArrayList<>();

    // To prepare a list of words from the vocabulary as needed by a specific generator algorithm
    @Override
    public ArrayList<String> prepareWords() {
        ArrayList<String> vocabulary = getVocabulary();

        // We want the the number range to be [1, total number of words] not [0, total number of words - 1]
        // Max number words can be of 1.5x length of the vocabulary array
        int numberOfWords = (int) ((Math.random()*100000000)%(vocabulary.size() + vocabulary.size()/2) ) + 1;
        ArrayList<String> words = new ArrayList<>(numberOfWords);

        try {
            for (int i = 0; i < numberOfWords; i++) {
                int randomIndex = (int) ((Math.random() * 100000000) % vocabulary.size());
                words.add(vocabulary.get(randomIndex));
            }

            // Sort the words
            Collections.sort(words);

            return words;
        } catch (IndexOutOfBoundsException exception){
            if(vocabulary.isEmpty())
                System.out.println("Vocabulary is empty. Please try adding some words first.");
            else
                System.out.println("Exception Occurred: Vocabulary index out of bound. " + exception.getLocalizedMessage());

            return null;
        }
    }

    // Concatenates the words generated by the prepareWords() method
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
    public String setCaseAndFormat(String word) {
        // Convert the word into lower case

        return word.toLowerCase();
    }

    @Override
    public void addToVocabulary(String formattedWord) {
        vocabulary = getVocabulary();
        vocabulary.add(formattedWord);
    }


    // Special getter method is needed since static field needs a static method to be accessed
    public static ArrayList<String> getVocabulary() {
        return vocabulary;
    }
}

class OrderedSentenceGenerator implements GeneratorStrategy{

    // To hold the vocabulary for this generator
    private static ArrayList<String> vocabulary = new ArrayList<>();

    // To prepare a list of words from the vocabulary as needed by a specific generator algorithm
    @Override
    public ArrayList<String> prepareWords() {
        ArrayList<String> vocabulary = getVocabulary();

        if(vocabulary.isEmpty()){
            System.out.println("Vocabulary is empty. Please try adding some words first.");

            return null;
        }

        // Return a deep copy of the vocabulary instead so that it cannot be altered outside this class
        ArrayList<String> copyOfVocabulary = new ArrayList<>();
        copyOfVocabulary.addAll(vocabulary);
        return copyOfVocabulary;
    }

    // Concatenates the words generated by the prepareWords() method
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
    public String setCaseAndFormat(String word) {
        // Convert the word into lower case
        word = word.toUpperCase();
        String reverseWord = "";
        for(int i=word.length()-1; i>=0; i--){
            reverseWord = reverseWord.concat(String.valueOf(word.charAt(i)));
        }
        return reverseWord;
    }

    @Override
    public void addToVocabulary(String formattedWord) {
        vocabulary = getVocabulary();
        vocabulary.add(formattedWord);
    }

    // Special getter method is needed since static field needs a static method to be accessed
    public static ArrayList<String> getVocabulary() {
        return vocabulary;
    }
}