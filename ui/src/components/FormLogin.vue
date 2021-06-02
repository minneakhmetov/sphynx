<template>
    <div>
        <v-main>
            <v-container fluid fill-height>
                <v-layout row wrap align-center justify-center style="margin-bottom: 15%">
                    <v-flex xs12 md4 class="text-xs-center">
                        <v-card class="elevation-9">
                            <v-card-title>
                                <v-img
                                        :src="require('../assets/logo.svg')"
                                        contain
                                        height="9rem"
                                />
                            </v-card-title>
                            <v-card-text>
                                <v-form v-on:submit.prevent="submit" ref="form">
                                    <v-text-field
                                            prepend-icon="person"
                                            name="loginOrEmail"
                                            v-model="loginOrEmail"
                                            label="Login or Email"
                                            type="text"
                                            :rules="[rules.required]"
                                            required
                                    ></v-text-field>
                                    <v-text-field
                                            v-model="password"
                                            prepend-icon="person"
                                            :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                                            :rules="[rules.required]"
                                            :type="showPassword ? 'text' : 'password'"
                                            label="Password"
                                            @click:append="showPassword = !showPassword"
                                    ></v-text-field>
                                    <v-btn
                                            elevation="0"
                                            block
                                            class="sphynx-secondary mt-4"
                                            type="submit"
                                            :loading="this.loading"
                                    >
                                        Login
                                    </v-btn>
                                </v-form>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                </v-layout>
            </v-container>
        </v-main>
    </div>
</template>

<script>
    import '../css/styles.sass'
    import router from "../router";
    import {mapActions} from 'vuex';

    export default {
        name: "FormLogin",
        data() {
            return {
                loading: false,
                showPassword: false,
                password: '',
                loginOrEmail: '',
                rules: {
                    required: value => !!value || 'Required',
                    // min: v => v.length >= 8 || 'Min 8 characters',
                    // emailMatch: () => (`The email and password you entered don't match`),
                }
            }
        },
        methods: {
            ...mapActions(["login"]),
            submit() {
                if (this.$refs.form.validate()) {
                    this.loading = true;
                    const body = {
                        loginOrEmail: this.loginOrEmail,
                        password: this.password
                    };
                    this.login(body).then(() => {
                        this.loading = false;
                        router.push("/main").catch(() => {
                        });
                    })
                }
            }
        }
    }
</script>

<style scoped>

</style>