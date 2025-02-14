package com.ywx.ble

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.UnsupportedEncodingException
import java.util.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import com.blankj.utilcode.util.ServiceUtils


object BluetoothToolManager {

    private val TAG = BluetoothToolManager::class.java.simpleName
    var HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb"
    private  var mStringBuffer = StringBuffer()

    //蓝牙service,负责后台的蓝牙服务
    private var mBluetoothLeService: BluetoothLeService? = null

    private var mGattCharacteristics = java.util.ArrayList<java.util.ArrayList<BluetoothGattCharacteristic>>()

    //蓝牙特征值
    private var target_chara: BluetoothGattCharacteristic? = null

    //蓝牙连接状态
    public var mConnected = false
    private var status = "disconnected"

    //蓝牙名字
    private var mDeviceName: String? = null

    //蓝牙地址
    private var mDeviceAddress: String? = null

    //蓝牙信号值
    private var mRssi: String? = null

    private val mhandler = Handler()

    private var mCallback: Callback? = null


    fun initialize(context: Context, DeviceAddress: String) {
//        val intent = Intent(context, serviceClass as Class<BluetoothLeService>)
//        context.startService(intent)
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        else
            ServiceUtils.unbindService(mServiceConnection)
        mDeviceAddress=DeviceAddress
        val gattServiceIntent = Intent(context, BluetoothLeService::class.java)
        ServiceUtils.bindService(
            gattServiceIntent,
            mServiceConnection,
            1//QuickActivity.BIND_AUTO_CREATE
        )
    }

    @Subscribe
    fun onGattReceiverEvent(event: GattReceiverEvent) {
        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(event.eventType)) //Gatt连接成功
        {
            mConnected = true
            status = "connected"
            EventBus.getDefault().post(DevicesConnectionsEvent(true))
        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(event.eventType)) {
            mConnected = false
            status = "disconnected"
            // EventBus.getDefault().post(new DataEvent(new String(data)));
            EventBus.getDefault().post(DevicesConnectionsEvent())
            //更新连接状态
            //updateConnectionState(status)

        } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED //发现GATT服务器
                .equals(event.eventType)) {
            // Show all the supported services and characteristics on the
            // user interface.
            //获取设备的所有蓝牙服务
            displayGattServices(mBluetoothLeService?.getSupportedGattServices())
            println("BroadcastReceiver :"
                    + "device SERVICES_DISCOVERED")
        } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(event.eventType)) //有效数据
        {
            //处理发送过来的数据
            try {
//                if (intent.extras!!.getString(
//                        BluetoothLeService.EXTRA_DATA) != null) {
//                    intent.extras!!.getString(BluetoothLeService.EXTRA_DATA)?.let { displayData(it, intent) }
//                    println("BroadcastReceiver onData:"
//                            + intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
//                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Subscribe
    fun onDataEvent(event: DataEvent) {
       // BluetoothDataSWManager.dealReceiveData(event.data)
    }

    private fun displayGattServices(gattServices: List<BluetoothGattService>?) {
        if (gattServices == null) return
        var uuid: String? = null
        val unknownServiceString = "unknown_service"
        val unknownCharaString = "unknown_characteristic"

        // 服务数据,可扩展下拉列表的第一级数据
        val gattServiceData = ArrayList<HashMap<String, String>>()

        // 特征数据（隶属于某一级服务下面的特征值集合）
        val gattCharacteristicData = ArrayList<ArrayList<HashMap<String, String>>>()

        // 部分层次，所有特征值集合
        mGattCharacteristics = ArrayList<ArrayList<BluetoothGattCharacteristic>>()

        // Loops through available GATT Services.
        for (gattService in gattServices) {

            // 获取服务列表
            val currentServiceData = HashMap<String, String>()
            uuid = gattService.uuid.toString()

            // 查表，根据该uuid获取对应的服务名称。SampleGattAttributes这个表需要自定义。
            gattServiceData.add(currentServiceData)
            println("Service uuid:$uuid")
            val gattCharacteristicGroupData = java.util.ArrayList<HashMap<String, String>>()

            // 从当前循环所指向的服务中读取特征值列表
            val gattCharacteristics = gattService
                .characteristics
            val charas = java.util.ArrayList<BluetoothGattCharacteristic>()

            // Loops through available Characteristics.
            // 对于当前循环所指向的服务中的每一个特征值
            for (gattCharacteristic in gattCharacteristics) {
                charas.add(gattCharacteristic)
                val currentCharaData = HashMap<String, String>()
                uuid = gattCharacteristic.uuid.toString()
                if (gattCharacteristic.uuid.toString()
                    == HEART_RATE_MEASUREMENT) {

                    // 测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
                    mhandler.postDelayed(Runnable {
                        println("readCharacteristic")
                        mBluetoothLeService?.setCharacteristicNotification(gattCharacteristic, true)

                        // TODO Auto-generated method stub
                        mBluetoothLeService?.readCharacteristic(gattCharacteristic)
                    }, 200)
                    println("Client uuid:$uuid")

                    // 接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    // mBluetoothLeService.setCharacteristicNotification(
                    //         gattCharacteristic, true);
                    target_chara = gattCharacteristic

                    // 设置数据内容
                    // 往蓝牙模块写入数据
                    // mBluetoothLeService.writeCharacteristic(gattCharacteristic);
                }
                val descriptors = gattCharacteristic
                    .descriptors
                for (descriptor in descriptors) {
                    println("---descriptor UUID:"
                            + descriptor.uuid)
                    // 获取特征值的描述
                    mBluetoothLeService?.getCharacteristicDescriptor(descriptor)
                    // mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,
                    // true);
                }
                gattCharacteristicGroupData.add(currentCharaData)
            }
            // 按先后顺序，分层次放入特征值集合中，只有特征值
            mGattCharacteristics.add(charas)
            // 构件第二级扩展列表（服务下面的特征值）
            gattCharacteristicData.add(gattCharacteristicGroupData)
        }
    }


    var isOpen = false

    private fun formatData(data: String) {

    }

    private fun dealReceiveData(data: String) {
        // KLog.e("recData :", data)

    }

    fun sendCommand(command: String, callback: Callback?)
    {
        SendDataThread(command)
        Log.i("---------command-------", command)
        mCallback= callback
    }
    /*
     * 数据发送线程
     *
     * */
    class SendDataThread(sendData:String) : Runnable {
        val mSendData =sendData
        override fun run() {
            // TODO Auto-generated method stub
            var buff: ByteArray? = null
            try {
                //buff = mSendData.toByteArray(charset("GB2312")) 字符串
                buff = HexUtils.hexString2ByteArray(mSendData)

            } catch (e: UnsupportedEncodingException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val sendDatalens: IntArray = dataSeparate(buff!!.size)
            Log.d("AppRunArrayLenght", "buff.length:" + buff!!.size)
            var length = 0
            for (i in 0 until sendDatalens[0]) {
                val dataFor20 = ByteArray(20)
                for (j in 0..19) {
                    dataFor20[j] = buff[i * 20 + j]
                    ++length
                }
                println("here1")
                println("here1:" + String(dataFor20))
                Log.d("AppRunArrayLenght", "超出20")
                //target_chara.setValue(dataFor20);
                mBluetoothLeService?.writeCharacteristic(dataFor20)
                /*try {
                    Thread.sleep(12);//丢包处理
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
            if (sendDatalens[1] != 0) {
                println("here2")
                val lastData = ByteArray(buff.size)
                for (i in 0 until sendDatalens[1]) {
                    lastData[i] = buff[sendDatalens[0]  + i]
                    ++length
                }
//                var str: String? = null
//                try {
//                   // str = String(lastData, 0, sendDatalens[1], "GB2312")
//                } catch (e: UnsupportedEncodingException) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace()
//                }
                if (target_chara == null) {
                    //PictureThreadUtils.runOnUiThread(Runnable {
//                        Toast.makeText(
//                            this@Ble_Activity,
//                            "没建立连接，请检查设备...",
//                            Toast.LENGTH_SHORT
//                        ).show()
                  // })
                    return
                }
                Log.d("AppRunArrayLenght", "总发送长: $length")
                for (lastDatum in lastData) {
                    //Log.d("AppRunArrayLenght", "lastDatum= $lastDatum")
                }
                //target_chara.setValue(lastData);//   --->此行出空指针错误):
                mBluetoothLeService?.writeCharacteristic(lastData)
                mBluetoothLeService?.startSend(target_chara)
            }
        }

        init {
            Thread(this).start()
        }
    }

    fun dataSeparate(len: Int): IntArray {
        val lens = IntArray(2)
        lens[0] =0// len / 20
        lens[1] = len
        return lens
    }

    /**
     * 将16进制字符串转换为byte[]
     */
    fun hexString2ByteArray(bs: String?): ByteArray? {
        var bs = bs ?: return null
        var bsLength = bs.length
        if (bsLength % 2 != 0) {
            bs = "0$bs"
            bsLength = bs.length
        }
        val cs = ByteArray(bsLength / 2)
        var st: String
        var i = 0
        while (i < bsLength) {
            st = bs.substring(i, i + 2)
            cs[i / 2] = st.toInt(16).toByte()
            i = i + 2
        }
        return cs
    }
    //byte数组转String
    fun bytesToHexString(bArray: ByteArray): String? {
        val sb = StringBuffer(bArray.size)
        var sTemp: String
        for (i in bArray.indices) {
            sTemp = Integer.toHexString(0xFF and bArray[i].toInt())
            if (sTemp.length < 2) sb.append(0)
            sb.append(sTemp.uppercase(Locale.getDefault()))
        }
        var length = sb.length
        if (length == 1 || length == 0) {
            return sb.toString()
        }
        if (length % 2 == 1) {
            sb.insert(length - 1, " ")
            length = length - 1
        }
        var i = length
        while (i > 0) {
            sb.insert(i, " ")
            i = i - 2
        }
        return sb.toString()
    }



    private fun getCheckSums(hexStr: String): String {  //求校验和  取低八位
        var sum = 0
        for (i in 0..hexStr.length - 1 step 2) {
            sum += hexStr.substring(i, i + 2).toInt(16)
        }

        var binaryStr = sum.toString(2)

        if (binaryStr.length > 8) {  //转二进制 取最后八位
            binaryStr = binaryStr.substring(binaryStr.length - 8, binaryStr.length)
        }

        var hexStr = binaryStr.toInt(2).toString(16)
        if (hexStr.length == 1) {
            hexStr = "0$hexStr"
        }

        return hexStr
    }


    private fun getAnswerData(data: String): Array<String> {
        var hexStr = data.replace("a55a", "")  //去掉头部
        //hexStr = hexStr.replace("aa", "")  //去掉尾部
        hexStr = hexStr.substring(0, hexStr.length - 2)  //去掉尾部
        hexStr = hexStr.substring(0, hexStr.length - 2)    //去掉检验和

        hexStr = hexStr.substring(4, hexStr.length)  //去掉帧长度 4位

        val answerDevice = hexStr.substring(0, 2)  //应答设备标识
        val answerInstruction = hexStr.substring(2, 4)  ///应答命令
        val answerData = hexStr.substring(4, hexStr.length)

//        KLog.i(TAG, "设备标识:  $answerDevice,  命令 : $answerInstruction,   数据 :  $answerData")

        return arrayOf(answerDevice, answerInstruction, answerData)
    }

    /* BluetoothLeService绑定的回调函数 */
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onServiceConnected(componentName: ComponentName,
                                        service: IBinder
        ) {
            mBluetoothLeService = (service as BluetoothLeService.LocalBinder).getService()
            if (!mBluetoothLeService!!.initialize()) {
                //finish()
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            // 根据蓝牙地址，连接设备
            mBluetoothLeService!!.connect(mDeviceAddress)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBluetoothLeService = null
        }
    }

}