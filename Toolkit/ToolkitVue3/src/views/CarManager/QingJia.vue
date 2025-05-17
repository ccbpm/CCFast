<template>
    <div>
      <iframe v-if="url" :src="url" scrolling="auto" frameborder="no" style="width: 100%; height: calc(100vh - 100px)" ></iframe>
    </div>
  </template>
  <script lang="ts" setup>
  import {  nextTick,ref, watch } from 'vue'
  import { useRoute } from 'vue-router';
  import Dev2UrlInterface from '@/Toolkit/Dev2UrlInterface';
  //获取Token
  const route = useRoute();
  const url = ref();
  const InitPage =  async () => {
      url.value = '';
      await nextTick();
      url.value = await Dev2UrlInterface.Flow_MyFlowByFlowNo('125');
      console.log(url.value);
  };

  InitPage();
  // 简单数据类型监听
  watch(() => route.query, () => {
    InitPage();
  })
  </script>
  <style lang="less">
  </style>