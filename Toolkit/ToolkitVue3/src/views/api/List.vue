<template>
  <div style="width: 100%;height: 100%;">
    <iframe v-if="url" :src="url" scrolling="auto" frameborder="no" style="width: 100%; height: 100%" ></iframe>
  </div>
</template>
<script setup lang="ts">
import {  ref } from 'vue';
const host = import.meta.env.VITE_GLOB_APP_URL   //获取请求地址
const platform = import.meta.env.VITE_GLOB_PLATFORM;
const url :any= ref();
const InitPage=()=>{
  url.value='';

  if (platform == 'CCFlow') {
    url.value = host + 'swagger/';
    } else {
      url.value =  host + '/swagger-ui.html';
    }
}
InitPage();

</script>
<style lang="less"></style>
