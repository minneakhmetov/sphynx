<template>
    <v-container>
        <v-row>
            <v-col>
                <v-btn
                        elevation="0"
                        class="sphynx-secondary mt-4 mr-2"
                        @click="setState('/execution/resume/', 'RUNNING')"
                        v-if="buttonIcon.includes('RESUME')"
                >
                    <v-icon class="mr-2">mdi-play-pause</v-icon>
                    Resume
                </v-btn>
                <v-btn
                        elevation="0"
                        class="sphynx-secondary mt-4 mr-2"
                        @click="setState('/execution/pause/', 'PAUSED')"
                        v-if="buttonIcon.includes('PAUSE')"
                >
                    <v-icon class="mr-2">mdi-pause</v-icon>
                    Pause
                </v-btn>
                <v-btn
                        elevation="0"
                        class="sphynx-secondary mt-4"
                        @click="setState('/execution/terminate/', 'TERMINATED')"
                        v-if="buttonIcon.includes('TERMINATE')"
                >
                    <v-icon class="mr-2">mdi-stop</v-icon>
                    Terminate
                </v-btn>
            </v-col>
        </v-row>
        <v-row class="mt-3">
            <v-col>
                <v-progress-linear
                        :indeterminate="getPrepare"
                        :value="process"
                        height="25"
                        :color="getStyleColor"
                >
                    <v-row>
                        <v-col style="text-align: center">
                            <strong>{{ (execution ? execution.state : '') + ' ' + Math.ceil(process) }}%</strong>
                        </v-col>
                        <v-col v-if="execution" cols="3" style="text-align: right">
                            <h4 class="mr-3">Elapsed Time: {{elapsedTime}}</h4>
                        </v-col>
                    </v-row>
                </v-progress-linear>
            </v-col>
        </v-row>
        <v-row>
            <v-col>
                <canvas id="chart">
                </canvas>
            </v-col>
        </v-row>
        <v-row v-for="(message, i) in messages" :key="i">
            <v-col>
                <v-alert
                        prominent
                        type="error"
                >
                    {{message}}
                </v-alert>
            </v-col>
        </v-row>
        <v-row>
            <v-col class="pb-0">
                <h3>
                    Metrics:
                </h3>
            </v-col>
        </v-row>
        <v-row>
            <v-col>
                <v-card
                        class="mx-auto"
                        tile
                        elevation="0"

                >
                    <div v-if="metrics.length">
                        <v-row align="center" justify="center" style="height: 4.8rem">
                            <v-col cols="1">
                                <div align="center">
                                    <p class="mb-0">Number</p>
                                </div>
                            </v-col>
                            <v-col cols="2">
                                <div align="center">
                                    <p class="mb-0">
                                        Iteration [number][name]
                                    </p>
                                </div>
                            </v-col>
                            <v-col cols="3">
                                <div align="center">
                                    <p class="mb-0">
                                        Time
                                    </p>
                                </div>
                            </v-col>
                            <v-col cols="3">
                                <div align="center">
                                    <p class="mb-0">
                                        With connections (ms)
                                    </p>
                                </div>
                            </v-col>
                            <v-col cols="3">
                                <div align="center">
                                    <p class="mb-0">
                                        Without connections (ms)
                                    </p>
                                </div>
                            </v-col>
                        </v-row>
                        <v-list-item class="mb-4 elevation-3"
                                     v-for="(metric, i) in metrics"
                                     :key="i"
                        >
                            <v-list-item-content>
                                <v-row align="center" justify="center" style="height: 4.8rem">
                                    <v-col cols="1">
                                        <div align="center">
                                            <h2>
                                                {{`#${metrics.length - i}.`}}
                                            </h2>
                                        </div>
                                    </v-col>
                                    <v-col cols="2">
                                        <div align="center">
                                            <p class="mb-0">
                                                {{`${metric.stepId}. ${metric.iterationName}`}}
                                            </p>
                                        </div>
                                    </v-col>
                                    <v-col cols="3">
                                        <div align="center">
                                            <p class="mb-0">
                                                {{getTime(metric.time)}}
                                            </p>
                                        </div>
                                    </v-col>
                                    <v-col cols="3">
                                        <div align="center">
                                            <p class="mb-0">
                                                {{metric.timeWithConnections}}
                                            </p>
                                        </div>
                                    </v-col>
                                    <v-col cols="3">
                                        <div align="center">
                                            <p class="mb-0">
                                                {{metric.timeWithoutConnections}}
                                            </p>
                                        </div>
                                    </v-col>
                                </v-row>
                            </v-list-item-content>
                        </v-list-item>
                    </div>
                    <div class="d-flex align-center justify-center pa-4 mx-auto" v-else-if="loading">
                        <v-progress-circular
                                indeterminate
                                color="amber darken-3"
                        ></v-progress-circular>
                    </div>
                    <div class="d-flex align-center justify-center pa-4 mx-auto" v-else>
                        <div class="text-subtitle-1">
                            There are no one execution now.
                        </div>
                    </div>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import Chart from 'chart.js';
    import testState from "../constants/teststate";
    import axios from "../axios";
    import stompClient from "../socket";

    export default {
        props: {
            id: String,
            testId: String
        },
        data: () => ({
            chart: null,
            buttonIcon: ['PAUSE', 'TERMINATE'],
            metrics: [],
            process: 0,
            messages: [],
            subscription: null,
            subscriptionExecution: null,
            loading: true,
            testState,
            execution: null,
            elapsedTime: '',
            chartConfig: {
                type: 'line',
                data: {
                    labels: [],
                    type: 'line',
                    datasets: [
                        {
                            label: 'With connections',
                            data: [],
                            backgroundColor: [
                                'rgba(255, 99, 132, 0)',
                            ],
                            borderColor: [
                                'rgba(255, 99, 132, 1)',
                            ],
                        },
                        {
                            label: 'Without connections',
                            data: [],
                            backgroundColor: [
                                'rgba(54, 162, 235, 0)',
                            ],
                            borderColor: [
                                'rgba(54, 162, 235, 1)',
                            ],
                        }
                    ]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'top',
                        },
                        title: {
                            display: true,
                            text: 'Chart.js Line Chart'
                        }
                    }
                },
            }
        }),
        mounted() {
            this.getMetrics();
            this.chart = new Chart(document.getElementById('chart'), this.chartConfig);
            // this.subscribe();
        },
        destroyed() {
            if (this.subscription && this.subscriptionExecution) {
                this.subscription.then(sub => sub.unsubscribe());
                this.subscriptionExecution.then(sub => sub.unsubscribe());
            }
        },
        methods: {
            setState(endpoint, state) {
                const id = this.$route.params.id;
                axios.post(endpoint + id).then(response => {
                    if (response) {
                        this.buttonIcon = testState[state].buttonIcon;
                    }
                });
            },
            subscribe() {
                const testId = this.$route.params.testId;
                const id = this.$route.params.id;
                this.subscription = stompClient.subscribe(`/test/${testId}/execution/${id}/metrics`, (updated) => {
                    let body = JSON.parse(updated.body);
                    this.metrics.unshift(body);
                    if (body.message){
                        this.messages.push(body.message);
                    }
                    this.chartConfig.data.datasets[0].data.push(body.timeWithConnections);
                    this.chartConfig.data.datasets[1].data.push(body.timeWithoutConnections);
                    this.chartConfig.data.labels.push(`${body.stepId}. ${body.iterationName}`);
                    this.process = body.process;
                    this.chart.update();
                }, {'id-sub': 0});
                this.subscriptionExecution = stompClient.subscribe(`/test/${testId}/execution/${id}`, (updated) => {
                    let body = JSON.parse(updated.body);
                    this.execution.testConfigsId = body.testConfigsId;
                    this.execution.state = body.state;
                    this.execution.startTime = body.startTime;
                    this.execution.endTime = body.endTime;
                    this.execution.userId = body.userId;
                    let state = testState[this.execution.state];
                    this.buttonIcon = state.buttonIcon;
                    if (state.finished) {
                        this.process = 100;
                    } else this.process = body.process;
                    if (body.message){
                        this.messages.push(body.message);
                    }
                    // this.getElapsedTime();
                    if (this.buttonIcon.includes('START')) {
                        this.subscription.then(sub => sub.unsubscribe());
                        this.subscriptionExecution.then(sub => sub.unsubscribe());
                        this.subscription = null;
                        this.subscriptionExecution = null;
                    }
                }, {'id-sub': 1});

            },
            getMetrics() {
                const id = this.$route.params.id;
                axios.get("/execution/" + id).then(response => {
                    if (response) {
                        this.execution = response.data;
                        let state = testState[this.execution.state];
                        this.buttonIcon = state.buttonIcon;
                        if (this.execution.message){
                            this.messages.push(this.execution.message);
                        }
                        this.getElapsedTime();
                        if (!state.finished) {
                            this.subscribe();
                        } else {
                            this.process = 100;
                        }
                    }
                });
                axios.get(`/execution/${id}/metrics`).then(response => {
                    if (response) {
                        let responseData = response.data;
                        this.chartConfig.data.datasets[0].data = responseData.map(metric => metric.timeWithConnections);
                        this.chartConfig.data.datasets[1].data = responseData.map(metric => metric.timeWithoutConnections);
                        this.chartConfig.data.labels = responseData.map(metric => `${metric.stepId}. ${metric.iterationName}`);
                        this.process = Math.max(...responseData.map(metrics => metrics.process), this.process);
                        this.metrics = responseData.reverse();
                        this.metrics.forEach(metric => {
                            if (metric.message){
                                this.messages.push(metric.message)
                            }
                        });
                        this.chart.update();
                        this.loading = false;
                    }
                });
            },
            getDate(array) {
                return new Date(array[0], array[1] - 1, array[2], array[3], array[4], array[5]);
            },
            getTime(array) {
                return `${this.getZero(array[2])}.${this.getZero(array[1])}.${array[0]} ${this.getZero(array[3])}:${this.getZero(array[4])}:${this.getZero(array[5])}`
            },
            getZero(number) {
                return number > 9 ? number : `0${number}`
            },
            timeDistance(date1, date2) {
                let distance = date1.getTime() - date2.getTime();
                const hours = Math.floor(distance / 3600000);
                distance -= hours * 3600000;
                const minutes = Math.floor(distance / 60000);
                distance -= minutes * 60000;
                const seconds = Math.floor(distance / 1000);
                return `${hours}:${('0' + minutes).slice(-2)}:${('0' + seconds).slice(-2)}`;
            },
            getElapsedTime() {
                if (this.execution.endTime) {
                    this.elapsedTime = this.timeDistance(this.getDate(this.execution.endTime), this.getDate(this.execution.startTime))
                } else {
                    this.elapsedTime = this.timeDistance(new Date(), this.getDate(this.execution.startTime));
                    setTimeout(() => {
                        if (!this.execution.endTime) {
                            this.elapsedTime = this.timeDistance(new Date(), this.getDate(this.execution.startTime));
                            this.getElapsedTime();
                        }
                    }, 1000);
                }
            }
        },
        name: "MetricsView",
        computed: {
            getPrepare() {
                if (this.execution)
                    return testState[this.execution.state].prepare;
                else return false;
            },
            getStyleColor() {
                if (this.execution) {
                    return testState[this.execution.state].style.color;
                }
                return null;
            }
        }
    }
</script>

<style scoped>

</style>