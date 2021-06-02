const testState = {
    CREATED: {
        style: {
            color: '#42a5f5',
            fontSize: '140%'
        },
        icon: 'mdi-star-circle-outline',
        buttonIcon: ['START']
    },
    STARTED: {
        style: {
            color: '#66bb6a',
            fontSize: '140%'
        },
        buttonIcon: ['START'],
        prepare: true
    },
    PENDING: {
        style: {
            color: '#ab47bc',
            fontSize: '140%'
        },
        buttonIcon: ['PAUSE', 'TERMINATE'],
        prepare: true
    },
    ORDERED: {
        style: {
            color: '#ab47bc',
            fontSize: '140%'
        },
        buttonIcon: ['PAUSE', 'TERMINATE'],
        prepare: true
    },
    RUNNING: {
        style: {
            color: '#66bb6a',
            fontSize: '140%',
            fontStyle: 'italic'
        },
        buttonIcon: ['PAUSE', 'TERMINATE']
    },
    OVERDUE: {
        style: {
            color: '#f44336',
            fontSize: '140%'
        },
        icon: 'mdi-clock-time-three-outline',
        buttonIcon: ['START'],
        finished: true
    },
    CLEANING: {
        style: {
            color: '#42a5f5',
            fontSize: '140%'
        },
        icon: 'mdi-broom',
        buttonIcon: ['PAUSE', 'TERMINATE']
    },
    SKIPPED: {
        style: {
            color: '#ffb74d',
            fontSize: '140%'
        },
        icon: 'mdi-debug-step-over',
        buttonIcon: ['START'],
        finished: true
    },
    PAUSED: {
        style: {
            color: '#42a5f5',
            fontSize: '140%'
        },
        icon: 'mdi-pause-circle-outline',
        buttonIcon: ['RESUME', 'TERMINATE']
    },
    FAILED: {
        style: {
            color: '#f44336',
            fontSize: '140%'
        },
        icon: 'mdi-close-circle-outline',
        buttonIcon: ['START'],
        finished: true
    },
    FINISHED: {
        style: {
            color: '#66bb6a',
            fontSize: '140%'
        },
        icon: 'mdi-check-bold',
        buttonIcon: ['START'],
        finished: true
    },
    TERMINATED: {
        style: {
            color: '#f44336',
            fontSize: '140%'
        },
        icon: 'mdi-stop-circle-outline',
        buttonIcon: ['START'],
        finished: true
    },
    UNKNOWN: {
        style: {
            color: '#f44336',
            fontSize: '140%'
        },
        icon: 'mdi-help-circle-outline',
        buttonIcon: ['START'],
        finished: true
    }
};
export default testState;



