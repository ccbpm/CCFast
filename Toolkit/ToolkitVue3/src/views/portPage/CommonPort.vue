<template>
    <div>
      <iframe v-if="url" :src="url" scrolling="auto" frameborder="no" style="width: 100%; height: calc(100vh - 100px)" ></iframe>
    </div>
  </template>
  <script lang="ts" setup>
  import { nextTick, ref, watch } from 'vue'
  import { useRoute } from 'vue-router';
  import Dev2UrlInterface from '@/Toolkit/Dev2UrlInterface';
  //获取Token
  const route = useRoute();
  const url = ref();

  const InitPage =  async () => {
      url.value = '';
      await nextTick();
      //方案一:通过路由直接拼接路径跳转Port页面.
      // const queryString= QueryMerge(route.query);
      // const host = 'http://localhost:3009'
      // url.value = `${host}/#/WF/Port?${queryString}&token=${Token}`;
      //方案二:直接使用配置好的路径，使Port路径使用的更加明显.
      const query =  route.query
      switch (query.Name) {
        case '发起':
          url.value = await Dev2UrlInterface.Menu_Flow_Start()
          break;
        case '待办':
          url.value = await Dev2UrlInterface.Menu_Flow_Todolist()
          break;
        case '在途':
          url.value = await Dev2UrlInterface.Menu_Flow_Runing()
          break;
        case '近期':
          url.value = await Dev2UrlInterface.Menu_Flow_Nearly()
          break;
        case '已完成':
          url.value = await Dev2UrlInterface.Menu_Flow_Complate()
          break;
        case '抄送':
          url.value = await Dev2UrlInterface.Menu_Flow_CC()
          break;
        case '草稿':
          url.value = await Dev2UrlInterface.Menu_Flow_Draft()
          break;
        case '消息':
          url.value = await Dev2UrlInterface.Menu_Flow_Msg()
          break;
        case '综合查询':
          url.value = await Dev2UrlInterface.Menu_Flow_Search()
          break;
        case '流程':
          url.value = await Dev2UrlInterface.Menu_Admin_Flows()
          break;
        case '表单':
          url.value = await Dev2UrlInterface.Menu_Admin_Frms()
          break;
        case '组织':
          url.value = await Dev2UrlInterface.Menu_Admin_Orgs()
          break;
        case '数据源':
          url.value = await Dev2UrlInterface.Menu_Admin_DBSrc()
          break;
      }
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