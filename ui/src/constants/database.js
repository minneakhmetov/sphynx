import maximum from "./maximum";

const databaseTypes = [
    {
        name: 'MySQL',
        type: 'MYSQL',
        icon: require('../assets/mysql.svg'),
        fields: {
            host: [
                value => new RegExp("(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}").test(value) ||
                    new RegExp("(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))").test(value) ||
                    new RegExp("^((?!-)[A-Za-z0–9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$").test(value) || "Invalid address"
            ],
            port: [value => !!value && new RegExp("^((6553[0-5])|(655[0-2][0-9])|(65[0-4][0-9]{2})|(6[0-4][0-9]{3})|([1-5][0-9]{4})|([0-5]{0,5})|([0-9]{1,4}))$").test(value) || "Invalid port",],
            database: [value => !!value || "Invalid database name"],
            user: [value => !!value || "Invalid username"],
            password: [value => !!value || "Invalid password"]
        },
        configs: {
            connect: {
                name: 'Connect (reconnect for each iteration)',
                inputType: 'switch'
            }
        },
        iterationConfigs: {
            timeLimit: {
                rules: [value => value <= maximum.MAX_TIME_LIMIT || `Time Limit should be less than ${maximum.MAX_TIME_LIMIT}`],
                name: 'Time Limit (seconds)',
                inputType: 'text',
                defaultValue: 0,
                isNumber: true
            },
            clients: {
                rules: [value => value <= maximum.MAX_CLIENTS || `Clients should be less than ${maximum.MAX_CLIENTS}`],
                name: 'Clients',
                inputType: 'text',
                defaultValue: 1,
                isNumber: true
            }
        }
    },
    {
        name: 'PostgreSQL',
        type: 'POSTGRESQL',
        icon: require('../assets/postgresql.svg'),
        fields: {
            host: [
                value => new RegExp("(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}").test(value) ||
                    new RegExp("(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))").test(value) ||
                    new RegExp("^((?!-)[A-Za-z0–9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$").test(value) || "Invalid address"
            ],
            port: [value => !!value && new RegExp("^((6553[0-5])|(655[0-2][0-9])|(65[0-4][0-9]{2})|(6[0-4][0-9]{3})|([1-5][0-9]{4})|([0-5]{0,5})|([0-9]{1,4}))$").test(value) || "Invalid port",],
            database: [value => !!value || "Invalid database name"],
            user: [value => !!value || "Invalid username"],
            password: [value => !!value || "Invalid password"]
        },
        configs: {
            connect: {
                name: 'Connect (reconnect for each iteration)',
                inputType: 'switch'
            },
            vacuum: {
                name: 'Vacuum (vacuum full before test)',
                inputType: 'switch'
            }
        },
        iterationConfigs: {
            timeLimit: {
                rules: [value => value <= maximum.MAX_TIME_LIMIT || `Time Limit should be less than ${maximum.MAX_TIME_LIMIT}`],
                name: 'Time Limit (seconds)',
                inputType: 'text',
                defaultValue: 0,
                isNumber: true
            },
            clients: {
                rules: [value => value <= maximum.MAX_CLIENTS || `Clients should be less than ${maximum.MAX_CLIENTS}`],
                name: 'Clients',
                inputType: 'text',
                defaultValue: 1,
                isNumber: true
            }
        }
    }
];

class DbTypeManager {

    getDatabaseTypes() {
        return databaseTypes.map(inner => inner.type);
    }

    findDbByType(type) {
        return databaseTypes.find(item => item.type === type);
    }

    getDbTypes() {
        return databaseTypes;
    }

}


export default new DbTypeManager();
