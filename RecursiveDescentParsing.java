import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Assignment 3 Solution. Implementation of a recursive descent parser for an LL(1) grammar
 * @author Hussein Al Osman
 */

public class RecursiveDescentParsing {
  private String token = " ";  //stores the value of the current token
	private BufferedReader br;   
	
	/**
	 * Constructor
	 * @param filepath
	 */
	public RecursiveDescentParsing(File inputFile){
		
		try {
			
			br = new BufferedReader(new FileReader(inputFile));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Read one token for the input file
	 * @return the latest read token
	 */
	public String getNextToken(){
		String line = "";
		
		try {
		
			line = br.readLine();
		
		} 
		catch (IOException e) {
			e.printStackTrace();
		}	
		
		return line!=null?line: "";	
	}
	
	
	/**
	 * This method starts the parsing operations
	 * @return true if there are not syntax errors and false otherwise
	 */
	public boolean parse(){
		
		token = getNextToken();
		
		// Process the program non-terminal and THEN check if the last token is the dollar sign (end of stream symbol)
		if (!program() || !token.equals("$")){
			return false;
		}
		else {
			return true;	
		}	
	}
	
	/**
	 * Handle the following grammar rule:
	 * <program> ::= {<statement_list>}
	 * @return true if the corresponding grammar rule is respected and false otherwise
	 */
	public boolean program(){
		
		// the program terminal must start with a { terminal
		if(token.equals("{")) {
			// we have a match, get next token
			token = getNextToken();
			
			// process the statment_list non-terminal
			if(!statementList()){
				// somewhere while processing the statment_list non-terminal, we had a syntax error
				return false;
			}
			else{
				if (token.equals("}")){
					// we have a match, get next token
					token = getNextToken();
					return true;
				}
				else {
					// the } terminal is missing
					return false;
				}
			}
		}
		else {
			// the { terminal is missing
			return false;
		}
	}
	
	
	/**
	 * Handle the following grammar rule:
	 * <statement_list> ::= <statement> ; <statement_list’>
	 * @return true if the corresponding grammar rule is respected and false otherwise
	 */
	public boolean statementList(){
		
		// process the statement non-terminal
		if(!statement()){
			
			// somewhere while processing the statement non-terminal, we had a syntax error
			return false;
		}
		else if(token.equals(";")){
			// we have a match, get next token
			token = getNextToken();
			
			// process the statment_list' non-terminal
			return statementListPrime();
		}
		else{
			// we are missing the ; terminal
			return false;
		}
	}

	
	/**
	 * Handle the following grammar rules:
	 * <statement_list’> ::= <statement_list>
	 * <statement_list’> ::= epsilon
	 * @return true if the corresponding grammar rules are respected and false otherwise
	 */
	public boolean statementListPrime(){
		
		// The } token is obtained from the follow(<statement_list'>) set
		// Handle the <statement_list'>::=epsilon rule
		if(token.equals("}")){
			
			
			return true;
		}

		// process the statment_list non-terminal
		else return statementList(); 
		
	} 

	/**
	 * Handle the following grammar rules:
	 * <statement> ::=call: <procedure_call>
	 * <statement> ::=compute: <expression>
	 * @return true if the corresponding grammar rule is respected and false otherwise
	 */
	public boolean statement(){
		
		// Look for the call terminal
		if(token.equals("call")){
			// we have a match, get next token
			token = getNextToken();
			
			// Look for the : terminal
			if(token.equals(":")){
				// we have a match, get next token
				token = getNextToken();
				
				// return the procedure_call non-terminal
				return procedureCall();
			}
			
		}
		// Look for the call terminal
		else if(token.equals("compute")){
			// we have a match, get next token
			token = getNextToken();
			
			// Look for the : terminal
			if(token.equals(":")){
				// we have a match, get next token
				token = getNextToken();
				
				// return the expression non-terminal
				return expression();
			}
			
		}

		// we are missing one of the expected terminals
		return false;

	}
	
	/**
	 * Handle the following grammar rule:
	 * <procedure_call>::=id(<parameters>)
	 * @return true if the corresponding grammar rule is respected and false otherwise
	 */
	public boolean procedureCall(){
		// Look for the id terminal
		if(token.equals("id")){
			// we have a match, get next token
			token = getNextToken();
			
			// Look for the ( terminal
			if(token.equals("(")){
				// we have a match, get next token
				token = getNextToken();
				
				// Call the method associated with the parameters non-terminal
				if(parameters()){
					
					// Look for the ) termianl
					if(token.equals(")")){
						// we have a match, get next token
						token = getNextToken();
						return true;
					}
				}
			}
		}
		
		// we are missing one of the expected terminals
		return false;
	}
	
	
	
	/**
	 * Handle the following grammar rule:
	 * <parameters>::=<factor><parameters’>
	 * @return true if the corresponding grammar rule is respected and false otherwise
	 */
	public boolean parameters(){
		// Call the method associated with the factor non-terminal
		if (factor()){
			
			// Call the method associated with the parameters' non-terminal
			return parametersPrime();
		}
		// A syntax error was detected during the execution of the factor method
		else return false;
	}
	
	
	/**
	 * Handle the following grammar rules:
	 * <parameters’>::=,<parameters>
	 * <parameters’>::=epsilon
	 * @return true if the corresponding grammar rule is respected and false otherwise
	 */
	public boolean parametersPrime(){
		// Look for the , terminal
		if(token.equals(",")){
			// we have a match, get next token
			token = getNextToken();
			
			return parameters();
		}
		// Handle the <parameters’>::=epsilon using the follow(<parameters’>) set
		else if (token.equals(")")){
			return true;
		}
		else {
			
			// The expected ) terminal was not found
			return false;
		}
	}

	
	/**
	 * Handle the following grammar rule:
	 * <expression> ::=id=<factor><expression’>
	 * @return true if the corresponding grammar rule is respected and false otherwise
	 */
	public boolean expression(){
		
		// Look for the id terminal
		if(token.equals("id")){
			// we have a match, get next token
			token = getNextToken();
			
			// Look for the = terminal
			if(token.equals("=")){
				// we have a match, get next token
				token = getNextToken();
				
				// Call the method associated with the factor non-terminal
				if (factor()){
				
					// return the method associated with the expression' non-terminal
					return expressionPrime();
				}
				else {
					
					// A syntax error was detected in the execution of the factor method
					return false;
				}
				
			}
			
		}
		return false;
	}
	
	

	
	/**
	 * Handle the following grammar rules:
	 * <expression’> ::= + <factor>
	 * <expression’> ::= - <factor>
	 * <expression’> ::= epsilon
	 * @return true if the corresponding grammar rules are respected and false otherwise
	 */
	public boolean expressionPrime(){
		

		
		// the expression' rule must start with the + or - terminal (otherwise, it would be the epsilon rule)
		if(token.equals("+")){
			// we have a match, get next token
			token = getNextToken();
			
			// process the factor non-terminal
			return factor();
		}
		else if(token.equals("-")){
			
			// we have a match, get next token
			token = getNextToken();
			
			// process the factor non-terminal
			return factor();
		}
		else if (token.equals(";")){
			
			// return true since we have an epsilon rule
			return true;
		}	
		else {
			return false;
		}
	}

	
	/**
	 * Handle the following grammar rules:
	 * <factor> ::= id
	 * <factor> ::= num
	 * @return true if the corresponding grammar rules are respected and false otherwise
	 */
	public boolean factor(){
		
		// the factor rule must start with the id or num
		if (token.equals("id")) {
			// we have a match, get next token
			token = getNextToken();
			return true;
		}
		else if (token.equals("num")) {
			// we have a match, get next token
			token = getNextToken(); 
			return true;
		}	
		else{
			// we are missing the id or num terminals
			return false; 
		}	
	}
	
	


	/**
	 * Main method that create an instance of the RecursiveDescentParsing class and starts the parsing operations
	 * @param args
	 */
	public static void main(String [] args){
		
		if (args.length > 0){
			String filepath = args[0];
			
			File file = new File(filepath);
			
			// Check is the specified path belongs to a file
			if (file.isFile()){
				RecursiveDescentParsing parser = new RecursiveDescentParsing(file);
				
				// Parse the input file
				boolean success = parser.parse();
				
				// Display results
				if (success){
					System.out.println("SUCCESS: the code has been successfully parsed");
				}
				else {
					System.out.println("ERROR: the code contains a syntax mistake");
				}
			}
			else {
				// In case the specified argument does not correspond to a valid file path
				System.err.println("Specified file path is invalid");
			}
		}
		else {
			// No input file has been specified
			System.err.println("USAGE: Specify the path of the input file as an argument");
		}
		
	}
}
