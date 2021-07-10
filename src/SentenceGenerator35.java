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
    private static final String[] generatorList = {
            "Random Sentence Generator",
            "Sorted Sentence Generator",
            "Ordered Sentence Generator",
            "Exit"
    };

    private static final String[] operationList = {
            "Add a word to the vocabulary",
            "Generate a sentence",
            "Back"
    };

    // This method displays a prompt to the users and let them select the desired generator
    // Return true if "choose generator" prompt should be displayed again
    // Return false to exit the program
    static boolean chooseStringGenerator() {

        // Display the list of string generators
        for (int i = 1; i <= generatorList.length; i++) {
            System.out.printf("%d. %s\n", i, generatorList[i - 1]);
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
                throw new InputMismatchException();
            }


            // ------------------------------------------------------------------------------

            // Proper operation has been selected
            System.out.println("-------------------------------------");
            // Original array index and user input differ by 1 since arrays start from 0
            System.out.println(generatorList[operationID-1] + " Selected\n");

            // All string generation operations are executed through StringGenerator class by the client
            SentenceGeneratorContext sentenceGeneratorContext = new SentenceGeneratorContext();

            // Random sentence generator requested
            if(operationID == 1){
                sentenceGeneratorContext.setGeneratorStrategy(new RandomSentenceGenerator());
            }
            // Sorted sentence generator requested
            else if(operationID == 2){
                sentenceGeneratorContext.setGeneratorStrategy(new SortedSentenceGenerator());
            }
            // Ordered sentence generator requested
            else{
                sentenceGeneratorContext.setGeneratorStrategy(new OrderedSentenceGenerator());
            }

            // Keep showing the "Add or Generate" until user wants to go back to the previous menu
            while(chooseAddOrGenerate(sentenceGeneratorContext));

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
    static boolean chooseAddOrGenerate(SentenceGeneratorContext sentenceGeneratorContext){
        // Display the selection prompt
        for (int i = 1; i <= operationList.length; i++) {
            System.out.printf("%d. %s\n", i, operationList[i - 1]);
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
                sentenceGeneratorContext.executeVocabularyAddition(word);
            }
            // User wants to generate a sentence
            else if (operationID == 2) {
                String sentence = sentenceGeneratorContext.executeStringGeneration();
                System.out.println("\"" + sentence + "\"\n");
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
class SentenceGeneratorContext {
    // To hold the strategy set by the client
    private GeneratorStrategy generatorStrategy;
    private final static int MAX_NUMBER_OF_WORDS = 30;

    // Method to let the client set the strategy
    public void setGeneratorStrategy(GeneratorStrategy generatorStrategy){
        this.generatorStrategy = generatorStrategy;
    }

    // This method is responsible for actually generating the sentence using the interface
    public String executeStringGeneration(){
       try {
           ArrayList<String> words = generatorStrategy.prepareWords(MAX_NUMBER_OF_WORDS);
           String generatedSentence = generatorStrategy.concatenate(words);

           if(words == null || generatedSentence == null)
               throw new NullPointerException();

           return generatedSentence;
       }
       // If at any step before, null is returned or NullPointerException Occurs, they are caught here and an empty string is returned in that case
       catch (NullPointerException exception){
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
    ArrayList<String> prepareWords(int maxNumberOfWords);

    // Concatenates the words generated by the prepareWords() method
    String concatenate(ArrayList<String> words);

    // Sets the case and format before saving to the vocabulary as needed by a particular algorithm
    String setCaseAndFormat(String word);

    // Add the formatted word word to the vocabulary
    void addToVocabulary(String formattedWord);
}

/*
    Random Sentence Generator generates a sentence formed by random words from its vocabulary.
    Each word is converted to lowercase before adding to the vocabulary.
 */

class RandomSentenceGenerator implements GeneratorStrategy{

    // To hold the vocabulary for this generator
    private static ArrayList<String> vocabulary = new ArrayList<>();

    // To prepare a list of words from the vocabulary as needed by a specific generator algorithm
    @Override
    public ArrayList<String> prepareWords(int maxNumberOfWords)
    {
        ArrayList<String> vocabulary = getVocabulary();

        try {
            // Max possible number words is assumed to be a Constant
            // Take the max possible number of words OR the size of the vocabulary - whichever smaller.
            int currentWordLimit = Math.min(maxNumberOfWords, vocabulary.size());
            int numberOfWords = (int) ( ((Math.random()*100) % currentWordLimit) + 1);

            ArrayList<String> words = new ArrayList<>(numberOfWords);

            for (int i = 0; i < numberOfWords; i++) {
                int randomIndex = (int) ((Math.random() * 100000000) % vocabulary.size());
                words.add(vocabulary.get(randomIndex));
            }

            return words;
        }
        // In case the vocabulary is empty or the index is out of bound, IndexOutOfBoundsException is thrown which is caught here
        catch (IndexOutOfBoundsException exception){
            if(vocabulary.isEmpty())
                System.out.println("RSG: Vocabulary is empty. Please try adding some words first.");
            else
                System.out.println("RSG: Exception Occurred: Vocabulary index out of bound. " + exception.getLocalizedMessage());

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
        }
        // In case the word ArrayList is empty or the index is out of bound, IndexOutOfBoundsException is thrown which is caught here
        catch (IndexOutOfBoundsException exception){
            System.out.println("RSG: Exception occurred: The word list array is empty! ");
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

/*
    Sorted Sentence Generator generates a sentence formed by random words from its vocabulary.
    But then the chosen words are sorted unlike the previous generator. Only after the words are
    concatenated to form a sentence.
    Each word is converted to lowercase before adding to the vocabulary.
 */

class SortedSentenceGenerator implements GeneratorStrategy{

    // To hold the vocabulary for this generator
    private static ArrayList<String> vocabulary = new ArrayList<>();

    // To prepare a list of words from the vocabulary as needed by a specific generator algorithm
    @Override
    public ArrayList<String> prepareWords(int maxNumberOfWords) {
        ArrayList<String> vocabulary = getVocabulary();

        try {
            // Max possible number words is assumed to be a Constant
            // Take the max possible number of words OR the size of the vocabulary - whichever smaller.
            int currentWordLimit = Math.min(maxNumberOfWords, vocabulary.size());
            int numberOfWords = (int) ( ((Math.random()*100) % currentWordLimit) + 1);

            ArrayList<String> words = new ArrayList<>(numberOfWords);

            for (int i = 0; i < numberOfWords; i++) {
                int randomIndex = (int) ((Math.random() * 100000000) % vocabulary.size());
                words.add(vocabulary.get(randomIndex));
            }

            // Sort the words
            Collections.sort(words);

            return words;
        }
        // In case the vocabulary is empty or the index is out of bound, IndexOutOfBoundsException is thrown which is caught here
        catch (IndexOutOfBoundsException exception){
            if(vocabulary.isEmpty())
                System.out.println("SSG: Vocabulary is empty. Please try adding some words first.");
            else
                System.out.println("SSG: Exception Occurred: Vocabulary index out of bound. " + exception.getLocalizedMessage());

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
        }
        // In case the word is empty or the index is out of bound, IndexOutOfBoundsException is thrown which is caught here
        catch (IndexOutOfBoundsException exception){
            System.out.println("SSG: Exception occurred: The word list array is empty!");
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


/*
    Ordered Sentence Generator generates a sentence formed by concatenating all of the words from
    its vocabulary in the order they were inserted.
    Each word is converted to UPPERCASE and reversed before adding to the vocabulary.
 */
class OrderedSentenceGenerator implements GeneratorStrategy{

    // To hold the vocabulary for this generator
    private static ArrayList<String> vocabulary = new ArrayList<>();

    // To prepare a list of words from the vocabulary as needed by a specific generator algorithm
    @Override
    public ArrayList<String> prepareWords(int maxNumberOfWords) {
        ArrayList<String> vocabulary = getVocabulary();

        try {
            // We want the the number range to be [1, total number of words] not [0, total number of words - 1]

            // Max possible number words is assumed to be a Constant
            // Take the max possible number of words OR the size of the vocabulary - whichever smaller.
            int currentWordLimit = Math.min(maxNumberOfWords, vocabulary.size());
            int numberOfWords = (int) ( ((Math.random()*100) % currentWordLimit) + 1);

            // Take the max possible number of words OR the size of the vocabulary - whichever smaller.
            numberOfWords = Math.min(numberOfWords, vocabulary.size());

            // Take all the indices of the vocabulary in an array first.
            // Shuffle them
            // Take the first "numberOfWords" numbers
            // This ensures that the words are selected randomly
            ArrayList<Integer> vocIndices = new ArrayList<>(vocabulary.size());
            ArrayList<Integer> wordIndices = new ArrayList<>(numberOfWords);

            for(int i=0; i<vocabulary.size(); i++)
                vocIndices.add(i);
            Collections.shuffle(vocIndices);
            for(int i=0; i<numberOfWords; i++){
                wordIndices.add(vocIndices.get(i));
            }

            // Sort the selected indices so that they are in the word they were inserted
            Collections.sort(wordIndices);

            ArrayList<String> words = new ArrayList<>(numberOfWords);

            for(int i=0; i<numberOfWords; i++){
                words.add(vocabulary.get(wordIndices.get(i)));
            }

            return words;
        }
        catch (IndexOutOfBoundsException exception){
            if(vocabulary.isEmpty())
                System.out.println("OSG: Vocabulary is empty. Please try adding some words first.");
            else{
                System.out.println(exception.getMessage());
                System.out.println("OSG: Exception Occurred: Vocabulary index out of bound. " + exception.getLocalizedMessage());
            }

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
            // In case the word array is empty or the index is out of bound, IndexOutOfBoundsException is thrown which is caught here
        } catch (IndexOutOfBoundsException exception){
            System.out.println("OSG: Exception occurred: The word list array is empty!");
            return null;
        }
    }

    @Override
    public String setCaseAndFormat(String word) {
        // Convert the word into lower case
        word = word.toUpperCase();

        // Reverse the word
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