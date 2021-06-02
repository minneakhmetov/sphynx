const webhooksConfigs = [
    {
        type: 'TELEGRAM',
        picture: require('../assets/telegram-brands.svg'),
        text: 'Telegram',
        fields: {
            botId: {
                rules: [
                    value => !!value || 'Required.',
                    value => (value && value.length >= 3) || 'Min 3 characters',
                ],
                name: 'Bot Id',
                inputType: 'text',
                // defaultValue: '',
                isNumber: false
            },
            chatId: {
                rules: [
                    value => !!value || 'Required.',
                    value => (value && value.length >= 3) || 'Min 3 characters',
                ],
                name: 'Chat Id',
                inputType: 'text',
                // defaultValue: 0,
                isNumber: false
            }
        }
    },
    {
        type: 'EMAIL',
        fields: {},
        icon: 'mdi-at',
        text: 'Email',
    }
];

export default webhooksConfigs;