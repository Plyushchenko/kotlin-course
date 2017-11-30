grammar Fun;

/*Problem statement says to have this rule, though it's unused*/
file: block;

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

/*Kinda hack to avoid 'mutually left-recursive' error*/
expression:  binaryExpression | atomicExpression;

atomicExpression
    : functionCall
    | IDENTIFIER
    | LITERAL
    | '('expression ')';

printlnCall: 'println' '(' arguments ')';

functionCall: IDENTIFIER '(' arguments ')';

arguments: (expression (',' expression)*)?;

binaryExpression
    : atomicExpression operator = (DIV | MOD | MUL) expression
    | atomicExpression operator = (MINUS | PLUS) expression
    | atomicExpression operator = (GEQ | GT | LEQ | LT) expression
    | atomicExpression operator = (EQ | NEQ) expression
    | atomicExpression operator = AND expression
    | atomicExpression operator = OR expression
    ;

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
