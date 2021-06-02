<template>
    <div>
        <v-alert
                elevation="4"
                :value="alert.enabled"
                type="error"
                width="25rem"
                transition="scale-transition"
        >{{alert.message}}
        </v-alert>
    </div>
</template>

<script>
    export default {
        name: "AlertView",
        props: {
            alert: {
                enabled: Boolean,
                message: String,
            }
        }
    }
</script>

<style scoped>
    /*@keyframes fadeInAnimation {*/
    /*     0%   { opacity:0; }*/
    /*     100% { opacity:1; }*/
    /* }*/
    /*@-o-keyframes fadeInAnimation{*/
    /*    100% { opacity:1; }*/
    /*}*/
    /*@-moz-keyframes fadeInAnimation{*/
    /*    0%   { opacity:0; }*/
    /*    100% { opacity:1; }*/
    /*}*/
    /*@-webkit-keyframes fadeInAnimation{*/
    /*    0%   { opacity:0; }*/
    /*    100% { opacity:1; }*/
    /*}*/
    /*.fadeIn {*/
    /*    -webkit-animation: fadeInAnimation 1s;*/
    /*    -moz-animation: fadeInAnimation 1s;*/
    /*    -o-animation: fadeInAnimation 1s;*/
    /*    animation: fadeInAnimation 1s;*/
    /*}*/

    /*@keyframes fadeOutAnimation {*/
    /*    0%   { opacity:1; }*/
    /*    100% { opacity:0; }*/
    /*}*/
    /*@-o-keyframes fadeOutAnimation{*/
    /*    0%   { opacity:1; }*/
    /*    100% { opacity:0; }*/
    /*}*/
    /*@-moz-keyframes fadeOutAnimation{*/
    /*    0%   { opacity:1; }*/
    /*    100% { opacity:0; }*/
    /*}*/
    /*@-webkit-keyframes fadeOutAnimation{*/
    /*    0%   { opacity:1; }*/
    /*    100% { opacity:0; }*/
    /*}*/
    /*.fadeOut {*/
    /*    -webkit-animation: fadeOutAnimation 1s;*/
    /*    -moz-animation: fadeOutAnimation 1s;*/
    /*    -o-animation: fadeOutAnimation 1s;*/
    /*    animation: fadeOutAnimation 1s;*/
    /*}*/

</style>