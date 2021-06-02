<template>
    <v-container>

        <v-row justify="center">
            <v-dialog
                    v-model="deleteDialog"
                    max-width="290"
            >
                <v-card>
                    <v-card-title class="headline">
                        Are you sure to delete {{deleteCandidate ? deleteCandidate.login : null}}?
                    </v-card-title>

                    <v-card-text>
                        This action cannot be reverted
                    </v-card-text>

                    <v-card-actions>
                        <v-spacer></v-spacer>

                        <v-btn
                                color="red darken-1"
                                text
                                @click="deleteUser"
                        >
                            Continue
                        </v-btn>

                        <v-btn
                                color="blue darken-1"
                                text
                                @click="setDeleteDialog(false, null)"
                        >
                            Cancel
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>
        </v-row>
        <v-row>
            <v-col>
                <v-dialog
                        v-model="dialog"
                        persistent
                        max-width="600px"
                >
                    <template v-slot:activator="{ on, attrs }">
                        <v-btn
                                elevation="0"
                                class="sphynx-secondary mt-4"
                                v-bind="attrs"
                                v-on="on"
                        >
                            + Add new user
                        </v-btn>
                    </template>
                    <v-card>
                        <v-card-title>
                            <span class="headline">{{this.update ? 'Update ' + this.input.login : 'Adding new user'}}</span>
                        </v-card-title>
                        <v-card-text>
                            <v-container>
                                <v-form ref="form" v-on:submit.prevent="">
                                    <v-row>
                                        <v-text-field
                                                label="Login"
                                                :rules="loginRules"
                                                v-model="input.login"
                                                hide-details="auto"
                                                type="text"
                                                required
                                        ></v-text-field>
                                    </v-row>
                                    <v-row>
                                        <v-text-field
                                                label="Email"
                                                :rules="emailRules"
                                                v-model="input.email"
                                                hide-details="auto"
                                                type="text"
                                                required
                                        ></v-text-field>
                                    </v-row>
                                    <v-row>
                                        <v-text-field
                                                label="Password"
                                                :rules="update ? passwordUpdateRules : passwordRules"
                                                v-model="input.password"
                                                hide-details="auto"
                                                required
                                                type="password"

                                        ></v-text-field>
                                    </v-row>
                                    <v-row>
                                        <v-select
                                                :items="getUserRole.map(role => role.name)"
                                                v-model="input.role"
                                                label="Role"
                                                :rules="[(v) => !!v || 'Required']"
                                        ></v-select>
                                    </v-row>
                                </v-form>
                            </v-container>
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn
                                    color="blue darken-1"
                                    text
                                    @click="closeDialog"
                            >
                                Close
                            </v-btn>
                            <v-btn
                                    color="blue darken-1"
                                    text
                                    @click="saveOrUpdate"
                            >
                                Save
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
            </v-col>
        </v-row>
        <v-row>
            <v-col>
                <v-card
                        class="mx-auto"
                        tile
                        elevation="0"

                >
                    <div v-if="users.length">
                        <v-list-item class="mb-4 elevation-3"
                                     v-for="(user, i) in users"
                                     v-bind:key="i"
                        >
                            <v-list-item-content>
                                <v-row align="center" style="height: 4.8rem">
                                    <v-col cols="1" align="center">
                                        <v-icon style="margin-right: 3%" size="50">
                                            mdi-account
                                        </v-icon>
                                    </v-col>
                                    <v-col cols="1"
                                           style="display: flex; align-items: center; justify-content: flex-start">
                                        <p class="mr-3 mb-0" style="font-size: 120%; font-weight: bold">
                                            ID:
                                        </p>
                                        <p class="mb-0" style="font-size: 110%;">{{user.id}}
                                        </p>
                                    </v-col>
                                    <v-col cols="2"
                                           style="display: flex; align-items: center; justify-content: flex-start">
                                        <p class="mr-3 mb-0" style="font-size: 120%; font-weight: bold">Login:
                                        </p>
                                        <p class="mb-0" style="font-size: 110%;">{{user.login}}
                                        </p>
                                    </v-col>
                                    <v-col cols="3"
                                           style="display: flex; align-items: center; justify-content: flex-start">
                                        <p class="mr-3 mb-0" style="font-size: 120%; font-weight: bold">Email:
                                        </p>
                                        <p class="mb-0" style="font-size: 110%;">{{user.email}}
                                        </p>
                                    </v-col>
                                    <v-col cols="2"
                                           style="display: flex; align-items: center; justify-content: flex-start">
                                        <p class="mr-3 mb-0" style="font-size: 120%; font-weight: bold">Role:
                                        </p>
                                        <p class="mb-0" style="font-size: 110%;">{{getUserRole.find(role => role.key ===
                                            user.role).name}}
                                        </p>
                                    </v-col>
                                    <v-col cols="3" class="text-right"
                                           style="display: flex; align-items: center; justify-content: flex-end">
                                        <!--                                        <p style="font-size: 100%">Successful</p>-->
                                        <v-btn
                                                elevation="0"
                                                class="sphynx-secondary mr-3"
                                                @click.stop="openUpdateDialog(user)"
                                        >
                                            Edit
                                        </v-btn>
                                        <v-btn
                                                color="red"
                                                dark
                                                @click.stop="setDeleteDialog(true, user)"
                                        >
                                            <v-icon>mdi-delete</v-icon>
                                        </v-btn>
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
                            There are no one user now. <a @click="dialog = true">Create the first</a>
                        </div>
                    </div>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import axios from "../axios";
    import userRole from "../constants/userrole";
    import PasswordValidator from 'password-validator';

    let schema = new PasswordValidator();
    schema
        .has().uppercase()
        .has().lowercase()
        .has().digits(1)
        .has().symbols(1)
        .has().not().spaces();

    export default {
        name: "UserView",
        data: () => ({
            loading: true,
            dialog: false,
            users: [],
            input: {},
            update: false,
            deleteDialog: false,
            deleteCandidate: null,
            updateId: null,
            loginRules: [
                value => !!value || 'Required',
                value => new RegExp("[a-zA-Z0-9]").test(value) || 'Illegal Login'
            ],
            emailRules: [
                value => !!value || 'Required',
                value => new RegExp("[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+").test(value) || 'Illegal email'
            ],
            passwordRules: [
                value => !!value && value.length > 8 && value.length < 30 || 'Length should be more than 8 and less than 30',
                value => {
                    let messages = schema.validate(value, {list: true});
                    if (messages.length === 0) {
                        return true;
                    } else {
                        return messages[0] === 'spaces' ? 'Password should not contain space character' :
                            `Password should contain at least one ${messages[0]} character`;
                    }
                }
            ],
            passwordUpdateRules: [
                value => {
                    if (value) {
                        if (!(value.length > 8 && value.length < 30)){
                            return 'Length should be more than 8 and less than 30';
                        }
                        let messages = schema.validate(value, {list: true});
                        if (messages.length === 0) {
                            return true;
                        } else {
                            return messages[0] === 'spaces' ? 'Password should not contain space character' :
                                `Password should contain at least one ${messages[0]} character`;
                        }
                    } else return true;
                }]
        }),
        mounted() {
            this.getUsers();
        },
        methods: {
            getUsers() {
                this.loading = true;
                axios.get("/user/all").then(response => {
                    if (response) {
                        this.users = response.data.reverse();
                        this.loading = false;
                    }
                })
            },
            closeDialog() {
                this.$refs.form.reset();
                this.dialog = false;
                this.update = false;
                this.input = {};
            },
            openUpdateDialog(user) {
                this.update = true;
                this.input = {...user};
                this.input.role = userRole.find(role => role.key === this.input.role).name;
                this.dialog = true;
            },
            setDeleteDialog(deleteDialog, deleteCandidate) {
                this.deleteDialog = deleteDialog;
                this.deleteCandidate = deleteCandidate;
            },
            saveOrUpdate() {
                if (this.update) {
                    if (this.$refs.form.validate()) {
                        let body = {
                            login: this.input.login,
                            email: this.input.email,
                            password: this.input.password,
                            role: userRole.find(role => role.name === this.input.role).key
                        };
                        axios.patch("/user/" + this.input.id, body).then(response => {
                            if (response) {
                                let user = this.users.find(user => user.id === this.input.id);
                                user.id = response.data.id;
                                user.login = response.data.login;
                                user.email = response.data.email;
                                user.role = response.data.role;
                                this.closeDialog();
                            }
                        })
                    }
                } else {
                    if (this.$refs.form.validate()) {
                        this.input.role = userRole.find(role => role.name === this.input.role).key;
                        axios.put("/user/", this.input).then(response => {
                            if (response) {
                                this.users.unshift(response.data);
                                this.closeDialog();
                            }
                        })
                    }
                }

            },
            deleteUser() {
                axios.delete("/user/id/" + this.deleteCandidate.id).then(response => {
                    if (response) {
                        this.users = this.users.filter(element => element !== this.deleteCandidate);
                        this.setDeleteDialog(false, null);
                    }
                })
            }
        },
        computed: {
            getUserRole() {
                return userRole;
            }
        }
    }
</script>

<style scoped>

</style>