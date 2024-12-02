import java.io.*;
import java.util.*;
//import javafx.util.Pair;

class Pair {
  String lexeme;
  String token;

  public Pair(String lexeme, String token) {
    this.lexeme = lexeme;
    this.token = token;
  }
}

class Main {

  public static void readFile() throws IOException {
    String givenInput = "File1";
    ArrayList<Pair> lexUnits = new ArrayList<Pair>();
    Scanner scnr = new Scanner(new FileReader(givenInput));
    makeLexAndTokens(givenInput, lexUnits);

  }

  // This function is called when there are parentheses present in the source code
  public static void parenWork(ArrayList<Pair> lexUnits, String word) {
    String wordBefore = word.substring(0, word.indexOf("("));
    if (wordBefore.length() > 0) {
      // check if there is a word before the parenthesis if there is add to lexUnits
      lexUnits.add(new Pair(wordBefore, "<ident>"));
    }
    lexUnits.add(new Pair("(", "Open Paren"));
    if (String.valueOf(word.charAt(word.indexOf("(") + 1)).equals(")")) {
      lexUnits.add(new Pair(")", "Close Paren"));
      if (String.valueOf(word.charAt(word.indexOf(")") + 1)).equals("{")) {
        // This adds the brace to the lex units
        lexUnits.add(new Pair("{", "Open Brace"));
      }
    } else if (word.substring(word.indexOf("(") + 1).length() != 0) {
      // If there exists a word after "(" instead of ")", extract the word
      String wordAfter = word.substring(word.indexOf("(") + 1);
      if (wordAfter.equals("float")) {
        // If ther word after "(" equals the keyword, add that as a keyword lexical unit
        lexUnits.add(new Pair(wordAfter, "<keyword>"));
      } else {
        // If ther word after "(" does not equal the keyword, add that as a ident
        // lexical unit
        lexUnits.add(new Pair(wordAfter, "<ident>"));
      }
    }
  }

  // This is the function that will go through the source code and Print out the
  // lexemes and tokens
  public static void makeLexAndTokens(String givenInput, ArrayList<Pair> lexUnits) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(givenInput));
    System.out.println(givenInput);

    String line = br.readLine();
    while (line != null) {
      String[] words = line.split(" ");
      for (String word : words) {
        if (word.equals("float")) {
          lexUnits.add(new Pair(word, "<keyword>"));

        } else if (word.equals("(")) {
          parenWork(lexUnits, word);
        } else if (word.contains("(")) {
          // Run parseParen() if word containts "("
          parenWork(lexUnits, word);
        } else if (word.contains(",")) {
          // If word contains "," add word before comma and comma itself to lexUnit
          lexUnits.add(new Pair(word.substring(0, word.indexOf(",")), "<ident>"));
          lexUnits.add(new Pair(word.substring(word.indexOf(",")), "Comma"));
        } else if (word.contains(";")) {
          // If word contains ";" add word before semicolon and semicolon itself to
          // lexUnit
          lexUnits.add(new Pair(word.substring(0, word.indexOf(";")), "<ident>"));
          lexUnits.add(new Pair(word.substring(word.indexOf(";")), "Semicolon"));
        } else if (word.equals(")")) {
          // If ")", add to lexUnit
          lexUnits.add(new Pair(word, "Close Paren"));
        } else if (word.equals("{")) {
          // If "{", add to lexUnit
          lexUnits.add(new Pair(word, "Open Brace"));
        } else if (word.equals("}")) {
          // If "}", add to lexUnit
          lexUnits.add(new Pair(word, "Close Brace"));
        } else {
          switch (word) {
            case "=":
              // If "=", add to lexUnit
              lexUnits.add(new Pair(word, "Equal Sign"));
              break;
            case "*":
              // If "*", add to lexUnit
              lexUnits.add(new Pair(word, "Multiplication Sign"));
              break;
            case "/":
              // If word equals "/", add it to lexUnit
              lexUnits.add(new Pair(word, "Division Sign"));
              break;
            default:
              // Assumes that the word is a identifier if other cases do not work
              lexUnits.add(new Pair(word, "<ident>"));
          }
        }

      }
      line = br.readLine();

    }

    br.close();
    // Print lexical units
    for (Pair unit : lexUnits) {
      System.out.println(unit.lexeme + "\t" + unit.token);
    }

  }

  // Now what we're doing is reading each line of the input

  // handles when there is a semicolon in a String
  public static void semicolonWork(String value) {
    value = value.substring(0, value.length() - 1);
    System.out.println(value + " identifier");
    System.out.println("; semicolon");
  }

  // Start of Recursive-Descending Parser.
  public static void program() throws IOException {

    // Reads in the File.
    File myObj = new File("File1");
    Scanner scnr = new Scanner(myObj);
    String value = scnr.next();

    // Tests the first line from the file to see if it matches the parser.
    if (value.equals("float") && value.matches("[a-z]+") && scnr.hasNext()) {
      value = scnr.next();
    } else {
      System.out.println("The program cannot be generated by the Demo Language.");
      System.out.println("Doesn't follow the correct function initialization.");
      System.exit(0);
    }

    if (scnr.hasNext() && value.matches("[a-z]+")) {
      value = scnr.next();

    } else {
      System.out.println("The program cannot be generated by the Demo Language.");
      System.out.println("Doesn't follow the correct function initialization.");
      System.exit(0);
    }

    if (value.equals("(){") && scnr.hasNext()) {
      value = scnr.next();
    } else {
      System.out.println("The program cannot be generated by the Demo Language.");
      System.out.println("Doesn't follow the correct function initialization.");
      System.exit(0);
    }

    // Calls the next nonterminal functions to go through the rest of the file.
    value = Declares(value, scnr, true);

    if (!value.equals("float")) {
      System.out.println("The program cannot be generated by the Demo Language.");
      System.out.println("The program doesn't declare the identifiers correctly.");
      System.exit(0);
    }
    // System.out.println(value);
    // Creates the condition that after all the declared variables are done that
    // there is a string that follows.
    if (value.matches("[a-z]+") && scnr.hasNext()) {

      value = stmts(value, scnr);
    } else {
      System.out.println("The program cannot be generated by the Demo Language.");
      System.out.println("The program doesn't have a statement after the declared identifiers.");
      System.exit(0);
    }

    // Creates the condition that checks to see after finding that string there is
    // an assign operator.
    if (value.equals("=")) {
      value = assign(value, scnr);
    } else {
      System.out.println("The program cannot be generated by the Demo Language.");
      System.out.println("The program doesn't have an assignment operator after the next statement.");
      System.exit(0);
    }

    // Creates the condtition that checks to see after the assignment operator that
    // there is a string.
    if (scnr.hasNext() && value.matches("[a-z]+")) {
      value = scnr.next();

      if (value.equals("*") || value.equals("/")) {
        value = expr(value, scnr, true);
      } else {
        System.out.println("The program cannot be generated by the Demo Language.");
        System.out.println("The program doesn't follow the correct expression functions.");
        System.exit(0);
      }
    } else {
      System.out.println("The program cannot be generated by the Demo Language.");
      System.out.println("The program doesn't follow the correct expression functions.");
      System.exit(0);
    }

    // System.out.println(value);

    System.out.println("“The program is generated by the grammar.”");
  }

  // This is the declares function that will recursively check for correct
  // initialization.
  public static String Declares(String value, Scanner scnr, Boolean isValid) {
    // The condition statement is checking whether the float keyword comes before a
    // string followed by a semi-colon. It will be called recursively until one of
    // these rules are broken.
    if (isValid == true) {

      isValid = false;
      if (value.matches("[a-z]+") && scnr.hasNext() && value.equals("float")) {
        isValid = true;
        value = scnr.next();
        //
        if (scnr.hasNext() && isValid == true && (value.substring(value.length() - 1).equals(";"))) {

          value = scnr.next();

        } else {
          System.out.println("The program cannot be generated by the Demo Language.");
          System.out.println("One of the declared identifiers isn't declared correctly.");
          System.exit(0);
        }

      }
      return Declares(value, scnr, isValid);

    } else {
      // System.out.println(value);
      if (value.equals("data")) {
        value = "float";
      }

      return value;
    }
  }

  // This function makes sure there is a statement string following the
  // declaration of the float variables.
  public static String stmts(String value, Scanner scnr) {
    if (scnr.hasNext()) {
      value = scnr.next();
    }
    return value;
  }

  // This function makes sure there is a assignment operator following the
  // statement string.
  public static String assign(String value, Scanner scnr) {
    if (scnr.hasNext() && value.equals("=")) {
      value = scnr.next();
    }
    return value;
  }

  // This function is the expresion which goes through the line the statement is
  // assigned to and makes sure it has variables that are either being multiplied
  // or divided by. It does this in a recursive manner.
  public static String expr(String value, Scanner scnr, Boolean isValid) {
    if (isValid == true) {
      isValid = false;
      if ((value.equals("*") || value.equals("/")) && scnr.hasNext()) {
        isValid = true;
        value = scnr.next();
      } else {
        isValid = false;
      }
      if (scnr.hasNext() && isValid == true) {
        value = scnr.next();
      } else {
        isValid = false;
      }
      expr(value, scnr, isValid);
    }
    return value;
  }

  // Main function that starts the call to produce the lexemes and tokens and the
  // recursive-descent parser.
  public static void main(String[] args) throws IOException {

    readFile();
    program();

  }

}
