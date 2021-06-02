import { highlight } from 'sql-highlight'

const config = {
    html: true,
    classPrefix: 'sql-hl-',
    color: {
        keyword: '\x1b[35m',  // SQL reserved keywords
        function: '\x1b[31m', // Functions
        number: '\x1b[32m',   // Numbers
        string: '\x1b[32m',   // Strings
        special: '\x1b[33m',  // Special characters
        bracket: '\x1b[33m',  // Brackets (parentheses)
        clear: '\x1b[0m'      // Clear (inserted after each match)
    }
};

const sqlhighlight = (sqlString) => highlight(sqlString, config);

export default sqlhighlight;
