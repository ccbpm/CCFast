<template>
    <div>
      <iframe v-if="url" :src="url" scrolling="auto" frameborder="no" style="width: 100%; height: calc(100vh - 100px)" ></iframe>
    </div>
  </template>
  <script lang="ts" setup>
  import { nextTick, ref, watch } from 'vue'
  import { useRoute } from 'vue-router';
  import { useUserStore } from '@/stores/user'
//获取Token
  const userStore = useUserStore()
  const Token = userStore.getToken;
  const route = useRoute();
  const url = ref();
  const host = import.meta.env.VITE_GLOB_VUE3_URL   //获取vue3Host
  const InitPage =  async () => {
      await nextTick();
      url.value = `${host}/#/WF/Port?DoWhat=HomePage&token=${Token}`;
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