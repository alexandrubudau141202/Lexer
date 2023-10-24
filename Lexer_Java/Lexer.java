import java.io.*; 
import java.util.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    
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
                    peek = ' ';
                    return Token.div;
                case ';':
                    peek = ' ';
                    return Token.semicolon;
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
                } else if (Character.isDigit(peek)) {
                    String integer = "";
                    do{
                        integer += peek;
                        readch(br);
                    } while(Character.isDigit(peek));
                    return new NumberTok(integer);
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
        File text_file = new File("file.txt");
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
