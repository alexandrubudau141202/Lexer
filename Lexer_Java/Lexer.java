import java.io.*; 
import java.util.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    
    public static boolean scan(String s){ 
        int state = 0;
        int i = 0;
        while (state >= 0 && i<s.length()){
            final char c = s.charAt(i++);
            switch(state){
                case 0:
                    if(c == '_')
                        state = 1;
                    else if (Character.isLetter(c))
                        state = 2;
                    else 
                        state = -1;
                    break;
                case 1:
                    if(c == '_')
                        state = 1;
                    else if(Character.isLetter(c) || Character.isDigit(c))
                        state = 2;
                    else
                        state = -1;
                    break;
                case 2:
                    if(Character.isLetter(c) || Character.isDigit(c) || c == '_')
                        state = 2;
                    else 
                        state = -1; 
                    break;
            }
        }
        return state == 2;
    }
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }
    

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
    
        switch (peek) {
                case '!':
                    peek = ' ';
                    return Token.not;
                case '(':
                    peek = ' ';
                    return Token.lpt;
                case ')':
                    peek = ' ';
                    return Token.rpt;
                case '[':
                    peek = ' ';
                    return Token.lpq;
                case ']':
                    peek = ' ';
                    return Token.rpq;
                case '{':
                    peek = ' ';
                    return Token.lpg;
                case '}':
                    peek = ' ';
                    return Token.rpg;
                case '+':
                    peek = ' ';
                    return Token.plus;
                case '-':
                    peek = ' ';
                    return Token.minus;
                case '*':
                    peek = ' ';
                    return Token.mult;
                case '/':
                readch(br);
                    if (peek == '/') {
                    // Skip until the end of the line
                    while (peek != '\n' && peek != (char)-1) {
                        readch(br);
                    }
                    return lexical_scan(br); // Recursively continue scanning after the comment
                } else if(peek == '*') {
                    // Skip multi-line comments
                    readch(br);
                    while (true) {
                        if (peek == (char)-1) {
                            System.err.println("Multi-line comment must end with */");
                            return null;
                        }
                        if (peek == '\n') {
                            line++;
                       } 
                        if (peek != '*'){
                            readch(br);
                        }
                        if (peek == '*' && peek != (char)-1) {
                            readch(br);
                            if (peek == '/') {
                                break;
                            }
                        }
                    }
                    readch(br);
                    return lexical_scan(br); // recursive call to continue scanning
                }
                else{
                    //peek = ' ';
                    return Token.div;
                }
                case ';':
                    peek = ' ';
                    return Token.semicolon;
                    case '.':
                    return Token.dot;
                case ',':
                    peek = ' ';
                    return Token.comma;
                
                case '&':
                    readch(br);
                    if (peek == '&') {
                        peek = ' ';
                        return Word.and;
                    } else {
                        System.err.println("Erroneous character"+ " after & : "  + peek );
                        return null;
                    }
                    
            case '|':
                    readch(br);
                    if(peek == '|'){
                        peek = ' ';
                           return Word.or;
                       } else{
                        System.err.println("Erroneous character" + " after | : "  + peek );
                           return null;
                    }
            case ':':
                readch(br);
                    if (peek == '=') {
                        peek = ' ';
                        return Word.init;
                    } else {
                        System.err.println("Single : doesn't appear in the values table");
                        return null;
                    }
            case '<':
            readch(br);
            if(peek == '='){
                peek = ' ';
                return Word.le;
            } else if(peek == '>'){
                peek = ' ';
                return Word.ne;
            } else {
                //peek = ' '; In questo caso, non leggerebbe il carattere successivo.
                return Word.lt;
            }
          
            case '>':
            readch(br);
            if(peek == '='){
                peek = ' ';
                return Word.ge;
            } else
                return Word.gt;

            case '=':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.eq;
                } else{
                    return Word.assign;
                }
                
            case (char)-1:
                return new Token(Tag.EOF);

            default:
                if (Character.isLetter(peek)) {
                    String s = "";
                    do{
                        s += peek;
                        readch(br);
                    } while(Character.isLetter(peek) || Character.isDigit(peek));
                    switch(s){
                        case "assign":
                            return Word.assign;
                        case "to":
                            return Word.to;
                        case "if":
                            return Word.iftok;
                        case "else":
                            return Word.elsetok;
                        case "do":
                            return Word.dotok;
                        case "for":
                            return Word.fortok;
                        case "begin":
                            return Word.begin;
                        case "end":
                            return Word.end;
                        case "print":
                            return Word.print;
                        case "read":
                            return Word.read;
                        default:
                        return new Word(Tag.ID, s);
                    }
                }
                else if (Character.isDigit(peek)) {
                    String integer = "";
                    for(; Character.isDigit(peek); readch(br))
                        integer += peek;
                    if((integer.charAt(0) == '0') && !integer.equals("0")){
                        System.err.println("Error: starting number with 0 is unacceptable. " + integer);
                        return null;
                    }
                    else{
                        return new NumberTok(Integer.parseInt(integer));
                    }
                }
                else {
                        System.err.println("Erroneous character: " 
                                + peek );
                        return null;
                }
            }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String text_file = "file.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(text_file));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            System.out.println("Reached end of file");
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }

}
