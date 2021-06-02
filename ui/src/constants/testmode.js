import maximum from "./maximum";

const testModes = [
    {
        key: 'STEPPED',
        name: 'Stepped',
        fields: {
            steps: {
                rules: [value => value <= maximum.MAX_STEPS || `Steps should be less than ${maximum.MAX_STEPS.toString()}`],
                name: 'Steps',
                inputType: 'text'
            }
        }
    },
    {
        key: 'TIMED',
        name: 'Timed',
        fields: {
            timeLimit: {
                rules: [value => value <= maximum.MAX_TIME_LIMIT || `Time Limit should be less than ${maximum.MAX_TIME_LIMIT.toString()}`],
                name: 'Time Limit (seconds)',
                inputType: 'text'
            },
            recursive:  {
                name: 'Recursive',
                inputType: 'switch'
            }
        }
    }
];

class TestModeManager {

    getNames(){
        return testModes.map(testModes => testModes.name);
    }

    getByName(name){
        return testModes.find(testMode => testMode.name === name);
    }

    getByKey(key){
        return testModes.find(testMode => testMode.key === key);
    }

}

export default new TestModeManager();