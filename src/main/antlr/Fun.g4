grammar Fun;

/*Problem statement says to have this rule, though it's unused*/
file: block EOF;

block: statement*;

blockWithBraces: '{' block '}';

statement
    : function
    | variable
    | expression
    | whileLoop
    | ifOperator
    | assignment
    | returnStatement
    | printlnCall
    ;

function: 'fun' IDENTIFIER '(' parameterNames ')' blockWithBraces;

variable: 'var' IDENTIFIER ('=' expression)?;

parameterNames: (IDENTIFIER (',' IDENTIFIER)*)?;

whileLoop: 'while' '(' expression ')' blockWithBraces;

ifOperator: 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?;

assignment: IDENTIFIER '=' expression;

returnStatement: 'return' expression;

expression
    : functionCall                                                       #functionCallExpression
    | lhs = expression operator = (DIV | MOD | MUL) rhs = expression     #binaryExpression
    | lhs = expression operator = (MINUS | PLUS) rhs = expression        #binaryExpression
    | lhs = expression operator = (GEQ | GT | LEQ | LT) rhs = expression #binaryExpression
    | lhs = expression operator = (EQ | NEQ) rhs = expression            #binaryExpression
    | lhs = expression operator = AND rhs = expression                   #binaryExpression
    | lhs = expression operator = OR rhs = expression                    #binaryExpression
    | IDENTIFIER                                                         #identifierExpression
    | LITERAL                                                            #literalExpression
    | '('expression ')'                                                  #expressionInBrackets
    ;

printlnCall: 'println' '(' arguments ')';

functionCall: IDENTIFIER '(' arguments ')';

arguments: (expression (',' expression)*)?;

AND: '&&';
DIV: '/';
EQ: '==';
GEQ: '>=';
GT: '>';
LEQ: '<=';
LT: '<';
MINUS: '-';
MOD: '%';
MUL: '*';
NEQ: '!=';
OR: '||';
PLUS: '+';

IDENTIFIER: [a-zA-Z_][a-zA-Z_0-9]*;

LITERAL: '0' | (MINUS? [1-9][0-9]*);

COMMENT: '//' ~[\r\n]* -> skip;

WS: (' ' | '\t' | '\r'| '\n') -> skip;

