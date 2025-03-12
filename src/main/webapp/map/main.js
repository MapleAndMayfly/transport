// key.js is git-ignored
import { ApiSrc } from "../key.js";

const carIconSrc = './resources/CarIcon.png';
const originIconSrc = './resources/Origin.png';
const destinationIconSrc = './resources/Destination.png';
const POIIconSrc = {
    "pharmaProducer" : './resources/PharmaceuticalProducer.png',
    "steelProducer"  : './resources/SteelProducer.png',
    "woodProducer"   : './resources/WoodProducer.png',
    "pharmaProcessor": './resources/PharmaceuticalProcessor.png',
    "steelProcessor" : './resources/SteelProcessor.png',
    "woodProcessor"  : './resources/WoodProcessor.png'
};

const script = document.createElement('script');
script.src = ApiSrc;
script.onload = () => main();
script.onerror = () => console.error('高德地图 API 加载失败');
document.head.appendChild(script);

const updateInterval = 5000;    // 5000ms

const routes = [];
const POIs = [];
const cars = [];

let isStatusModifying = false;
let isRouteUpdating = false;
let isCarUpdating = false;

let map;
let driving;
let carIcon;

async function main()
{
    try
    {
        map = new AMap.Map("container", {
            center: [104.10248, 30.67646],
            zoom: 14
        });

        map.addControl(new AMap.ToolBar());
        map.addControl(new AMap.Scale());
        map.addControl(new AMap.ControlBar());

        console.log('地图初始化完成');

        await initPOI();
        await initCar();
    }
    catch (error)
    {
        console.error('初始化失败: ', error);
    }

    try
    {
        setInterval(() => update(), updateInterval);
    }
    catch (error)
    {
        console.error('运行时出错: ', error);
    }
}

/*----------------------------------------------------------------
 * 初始化
 * 
 * 
 * 
 * 
 * 
 * 
 */

async function initPOI()
{
    // 遍历 POIIconSrc 对象的所有键值对
    for (const [type, iconSrc] of Object.entries(POIIconSrc))
    {
        let data = await getPOIData(type);

        const icon = new AMap.Icon({
            size: new AMap.Size(16,16),
            image: iconSrc,
            imageSize: new AMap.Size(16,16)
        });

        data.forEach(poi => {
            const marker = new AMap.Marker({
                title: poi.name,
                position: new AMap.LngLat(poi.lon, poi.lat),
                icon: icon,
                map: map
            });

            POIs.push({
                UUID: poi.UUID,
                marker: marker
            });
        });
        console.log(`${type} 兴趣点添加成功, 数量: ${data.length}`);
    }
}

async function initCar()
{
    let data = await getCarData();

    let drivingOption = {
        policy: AMap.DrivingPolicy.LEAST_TIME,
        ferry: 1,
        province: '川'
    };
    driving = new AMap.Driving(drivingOption);

    carIcon = new AMap.Icon({
        size: new AMap.Size(32, 32),
        image: carIconSrc,
        imageSize: new AMap.Size(32, 32)
    });

    data.forEach(car => {
        const marker = new AMap.Marker({
            position: new AMap.LngLat(car.lon, car.lat),
            icon: carIcon,
            map: map
        });

        cars.push({
            UUID: car.UUID,
            marker: marker
        });
    });
    console.log(`车辆添加成功, 数量: ${data.length}`);
}

/**
 * 获取POI的数据信息列表
 * @param {string} type
 * @returns 包含UUID, name, lat, lon属性的类
 */
async function getPOIData(type)
{
    try
    {
        let url = new URL('/data/getPoiData', window.location.origin);
        url.searchParams.append("type", type);

        const response = await fetch(url);
        if (!response.ok) throw new Error('POI 数据获取失败');

        let data = await response.json();
        if (!Array.isArray(data)) throw new Error('POI 数据无效', { cause: data+'无效' });

        data.forEach(item => {
            let isValid = item.hasOwnProperty("UUID")
                       && item.hasOwnProperty("name")
                       && item.hasOwnProperty("lat")
                       && item.hasOwnProperty("lon");
            if (!isValid) throw new Error('POI 数据解析失败', { cause: item+'解析出错' });
        });
        return data;
    }
    catch (error)
    {
        console.error('POI 数据获取错误: ', error);
        return [{"Exception": "error occurs!"}];
    }
}

/**
 * 获取车辆的数据信息
 * @returns 包含UUID, lat, lon属性的类
 */
async function getCarData()
{
    try
    {
        let url = new URL('/data/getCarData', window.location.origin);

        let response = await fetch(url);
        if (!response.ok) throw new Error('车辆数据获取失败');

        let data = await response.json();
        if (!Array.isArray(data)) throw new Error('车辆数据无效', { cause: data+'无效' });

        data.forEach(item => {
            let isValid = item.hasOwnProperty("UUID")
                    && item.hasOwnProperty("lat")
                    && item.hasOwnProperty("lon");
            if (!isValid) throw new Error('车辆数据解析失败', { cause: item+'解析出错' });
        });
        return data;
    }
    catch (error)
    {
        console.error('车辆数据获取错误: ', error);
        return [{"Exception": "error occurs!"}];
    }
}

/**----------------------------------------------------------------
 * 更新数据与页面
 * 
 * 
 * 
 * 
 * 
 * 
 */
async function update()
{
    await updateCars();
    await updateRoutes();
}

/**
 * 车辆位置及状态更新
 */
async function updateCars()
{
    if (isCarUpdating) return;
    isCarUpdating = true;

    try
    {
        // TODO
    }
    catch (error)
    {
        console.error('车辆更新时出错: ', error);
    }
    finally
    {
        isCarUpdating = false;
    }
}

/*----------------------------------------------------------------
 * 下面的没看完
 * 
 * 
 * 
 * 
 * 
 */
async function updateRoutes()
{
    if (isRouteUpdating) return;
    
    isRouteUpdating = true;
    try
    {
        routes = routes.filter(routeInfo => {
            routeInfo.Tnum--;

            if (routeInfo.Tnum <= 0)
            {
                removeRoute(routeInfo.route1, routeInfo.car, routeInfo.demandStart1);
                removeRoute(routeInfo.route2, routeInfo.demandStart2, routeInfo.demandEnd);
                return false;
            }
            return true;
        });
    }
    catch (error)
    {
        console.error('路线更新错误: ', error);
    }
    finally
    {
        isRouteUpdating = false;
    }
}

function removeRoute(route, startMarker, endMarker)
{
    route.setMap(null);
    startMarker.setMap(null);
    endMarker.setMap(null);
}

function planRoute(start, end)
{
    return new Promise((resolve, reject) => driving.search(
            new AMap.LngLat(start[0], start[1]),
            new AMap.LngLat(end[0], end[1]),
            function (status, result)
            {
                if (status === 'complete' && result.routes && result.routes.length)
                {
                    resolve(result.routes[0]);
                }
                else
                {
                    reject(new Error('请求失败，状态: ' + status));
                }
            }
        )
    );
}

function storeRouteInfo(route1, route2, car, demandStart1, demandStart2,
                        demandEnd, Tnum, index)
{
    routes.push({
        route1: route1,
        route2: route2,
        car: car,
        demandStart1: demandStart1,
        demandStart2: demandStart2,
        demandEnd: demandEnd,
        Tnum: Tnum,
        index: index
    });
}

// 绘制路线函数，基本沿用原代码逻辑
function drawRoute(route)
{
    let path = parseRouteToPath(route);

    let startMarker = new AMap.Marker({
        position: path[0],
        icon: originIconSrc,
        map: map
    });

    let endMarker = new AMap.Marker({
        position: path[path.length - 1],
        icon: destinationIconSrc,
        map: map
    });

    route = new AMap.Polyline({
        path: path,
        isOutline: true,
        outlineColor: '#ffeeee',
        borderWeight: 2,
        strokeWeight: 5,
        strokeOpacity: 0.9,
        strokeColor: '#0091ff',
        lineJoin: 'round'
    });

    map.add(route);

    // 调整视野达到最佳显示区域
    // map.setFitView([startMarker, endMarker, route]);
    let LineInfo = {
        startMarker:startMarker,
        endMarker:endMarker,
        route:route
    };
    return LineInfo;
}

// 解析DrivingRoute对象获取路径数组，基本沿用原代码逻辑
function parseRouteToPath(route)
{
    let path = [];
    for (let i = 0, l = route.steps.length; i < l; i++)
    {
        let step = route.steps[i];
        for (let j = 0, n = step.path.length; j < n; j++)
        {
            path.push(step.path[j]);
        }
    }
    return path;
}