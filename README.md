这是实训课上老师让给了基础的思路，我们自己写的成品

主要运用到了高德定位

@AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    private void initLocation() {
        //高德定位
        //初始化client
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位参数
        mLocationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationChanged(AMapLocation loc) {
                if (null != loc) {
                    if(loc.getErrorCode()==0){
                        //定位获取对应的市行政名称
                        addressResolutionUtil = new AddressResolutionUtil();
                        String cityWeather = addressResolutionUtil.addressResolution(loc.getAddress()).get(0).get("city");
                        String substring = cityWeather.substring(0, cityWeather.length() - 1);
                        adapter.updateLocateState(CityRecyclerAdapter.SUCCESS,substring);
                        //如果为空则将key值为001得添加进去
                       if(!SbcUtils.contains(MainActivity.this,"001")){
                            SbcUtils.put(MainActivity.this,"001",substring);
                        }else{
                           //如果包含，先将文件内得数据全部清空，再将key值为001得添加进去
                            SbcUtils.clear(MainActivity.this);
                            SbcUtils.put(MainActivity.this,"001",substring);
                        }
                    }else{
                        adapter.updateLocateState(CityRecyclerAdapter.FAILED, null);
                        //打印错误输出结果
                        Log.e("errorInfo",loc.getErrorInfo());
                    }
                } else {
                    adapter.updateLocateState(CityRecyclerAdapter.FAILED, null);
                }
            }
        });
        //权限判断，当没有打开位置权限，会进行询问
        if (EasyPermissions.hasPermissions(this, perms)) {
            mLocationClient.startLocation();
        } else {
            EasyPermissions.requestPermissions(this, "定位需要相关权限",
                    RC_LOCATION_CONTACTS_PERM, perms);
        }
    }


private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

citypicker框架，首页加载数据库中的城市


app中的聚合天气请求数据接口免费条数已经使用完毕
