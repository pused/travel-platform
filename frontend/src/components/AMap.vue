<script setup>
import { computed, onMounted, onUnmounted, watch, ref } from "vue";
import AMapLoader from "@amap/amap-jsapi-loader";
import config from "@/config"
import { useRoute } from "vue-router";

const route = useRoute()
const props = defineProps({
  locationParams: {
    type: Object,
    default: () => ({})
  },
  width: {
    type: String,
    default: "100%"
  },
  height: {
    type: String,
    default: "600px"
  },
  // 新增：是否显示路线规划
  showRoute: {
    type: Boolean,
    default: false
  },
  // 新增：起点位置（用户位置）
  startLocation: {
    type: Object,
    default: () => null
  }
})

const emit = defineEmits(['routeCalculated'])

const location = computed(() => {
  // 优先使用传入的locationParams，否则使用路由参数
  if (props.locationParams && props.locationParams.lat && props.locationParams.lon) {
    return {
      lat: props.locationParams.lat,
      lon: props.locationParams.lon,
      name: props.locationParams.name || '位置'
    };
  }
  return {
    lat: route.query.lat,
    lon: route.query.lon,
    name: route.query.name,
  };
});
const cusStyle = computed(() => {
    return {
        width: props.width,
        height: props.height,
    }
});

let map = ref(null);
let driving = ref(null);
// 缓存 AMap 实例，避免全局 window 依赖导致插件失效
let AMapInstance = null;

// 初始化地图
const initMap = async () => {
  // 验证坐标有效性
  const lon = parseFloat(location.value.lon);
  const lat = parseFloat(location.value.lat);
  
  if (!lon || !lat || isNaN(lon) || isNaN(lat)) {
    console.error('地图坐标无效:', location.value);
    return;
  }
  
  window._AMapSecurityConfig = {
    securityJsCode: config.safetyKey,
  };
  AMapInstance = await AMapLoader.load({
    key: config.mapKey, // 申请好的Web端开发者Key，首次调用 load 时必填
    version: "2.0", // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
    plugins: ["AMap.Scale", "AMap.Driving"], //需要使用的的插件列表，添加路线规划插件
  })
  
  // 如果地图已存在，先销毁
  if (map.value) {
    map.value.destroy();
  }
  
  map.value = new AMapInstance.Map("container", {
    // 设置地图容器id
    viewMode: "3D", // 是否为3D地图模式
    zoom: 11, // 初始化地图级别
    center: [lon, lat], // 初始化地图中心点位置
  });

  // 如果需要显示路线且有起点位置，则规划路线
  if (props.showRoute && props.startLocation) {
    planRoute();
  } else {
    // 否则只显示目标位置标记
    addMarker(lon, lat, location.value.name)
  }
}

// 添加地图标记
const addMarker = (lon, lat, name, isStart = false) => {
  if (!map.value || !AMapInstance) return;
  const marker = new AMapInstance.Marker({
    position: new AMapInstance.LngLat(lon, lat), //经纬度对象，也可以是经纬度构成的一维数组[116.39, 39.9]
    title: name,
    label: {
      content: `${name}`,
      direction: "top",
      offset: new AMapInstance.Pixel(0, -10)
    },
    icon: new AMapInstance.Icon({
      size: new AMapInstance.Size(32, 32),
      image: isStart 
        ? "https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png" // 蓝色标记表示起点
        : "https://webapi.amap.com/theme/v1.3/markers/n/mark_r.png"  // 红色标记表示终点
      })
    });
    //将创建的点标记添加到已有的地图实例：
    map.value.add(marker);
    marker.on("click", function (e) {
  });
}

// 规划路线
const planRoute = () => {
  if (!map.value || !props.startLocation || !AMapInstance) return;
  
  // 处理起点位置格式（支持数组和对象两种格式）
  let startLon, startLat;
  if (Array.isArray(props.startLocation)) {
    startLon = parseFloat(props.startLocation[0]);
    startLat = parseFloat(props.startLocation[1]);
  } else {
    startLon = parseFloat(props.startLocation.lon);
    startLat = parseFloat(props.startLocation.lat);
  }
  
  // 验证坐标有效性
  if (!startLon || !startLat || isNaN(startLon) || isNaN(startLat)) {
    console.error('起点坐标无效:', props.startLocation);
    return;
  }
  
  // 清除之前的路线
  if (driving.value) {
    driving.value.clear();
  }
  // 清理之前的点标记，防止多次叠加
  map.value.clearMap();
  // 标注起点和终点
  addMarker(startLon, startLat, "我的位置", true);
  addMarker(location.value.lon, location.value.lat, location.value.name || "目的地", false);
  
  // 创建驾车路线规划实例
  driving.value = new AMapInstance.Driving({
    map: map.value,
    showTraffic: false,
    hideMarkers: false,
    autoFitView: true,
    policy: AMapInstance.DrivingPolicy.LEAST_TIME, // 最快捷模式
  });

  // 根据起终点经纬度规划驾车导航路线
  driving.value.search(
    new AMapInstance.LngLat(startLon, startLat), // 起点
    new AMapInstance.LngLat(location.value.lon, location.value.lat), // 终点
    function (status, result) {
      if (status === 'complete') {
        console.log('路线规划成功', result);
        // 发送路线信息给父组件
        emit('routeCalculated', {
          distance: result.routes[0].distance,
          time: result.routes[0].time,
          routes: result.routes
        });
      } else {
        console.error('路线规划失败', result);
        // 失败时至少保留目的地标记
        map.value.clearMap();
        addMarker(location.value.lon, location.value.lat, location.value.name || "目的地");
      }
    }
  );
}

// 清除路线
const clearRoute = () => {
  if (driving.value) {
    driving.value.clear();
  }
  if (map.value) {
    map.value.clearMap();
  }
}

// 暴露方法给父组件
defineExpose({
  planRoute,
  clearRoute
})

onUnmounted(() => {
  if (driving.value) {
    driving.value.clear();
  }
  map.value?.destroy();
});

// 初始化地图
onMounted(() => {
  initMap();
});

// 监听位置参数变化
watch(() => props.locationParams, (newVal, oldVal) => {
  if (newVal !== oldVal && map.value) {
    // 重新初始化地图
    initMap();
  }
}, {deep: true});

// 监听路线显示状态变化
watch(() => [props.showRoute, props.startLocation], () => {
  if (map.value) {
    if (props.showRoute && props.startLocation) {
      planRoute();
    } else {
      clearRoute();
      // 只显示目标位置标记
      if (location.value.lon && location.value.lat) {
        addMarker(location.value.lon, location.value.lat, location.value.name);
      }
    }
  }
}, {deep: true})
</script>

<template>
  <div :style="cusStyle" id="container"></div>
</template>

<style scoped lang="scss">
#container {
  width: 100%;
  height: 800px;
}
</style>
