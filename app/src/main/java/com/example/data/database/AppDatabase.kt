package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CustomerDao
import com.example.data.dao.DiagramDao
import com.example.data.dao.FileDao
import com.example.data.dao.LcdDao
import com.example.data.dao.ModelDao
import com.example.data.entity.CustomerEntity
import com.example.data.entity.DiagramEntity
import com.example.data.entity.FileEntity
import com.example.data.entity.LcdEntity
import com.example.data.entity.ModelEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        CustomerEntity::class,
        ModelEntity::class,
        DiagramEntity::class,
        LcdEntity::class,
        FileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun modelDao(): ModelDao
    abstract fun diagramDao(): DiagramDao
    abstract fun lcdDao(): LcdDao
    abstract fun fileDao(): FileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gsm_service_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val modelDao = database.modelDao()
            val diagramDao = database.diagramDao()
            val lcdDao = database.lcdDao()
            val fileDao = database.fileDao()
            val customerDao = database.customerDao()

            // Pre-seed Phone Models
            val initialModels = listOf(
                // Samsung
                ModelEntity(brand = "Samsung", modelName = "Galaxy A05", modelNumber = "SM-A055F", chipset = "MediaTek Helio G85 (12nm)", androidVersion = "Android 14 (One UI 6.0)", ram = "4GB / 6GB", storage = "64GB / 128GB", network = "4G LTE", battery = "5000 mAh", charging = "25W Fast Charging", notes = "Type-C 2.0, dual camera 50MP"),
                ModelEntity(brand = "Samsung", modelName = "Galaxy A05s", modelNumber = "SM-A057F", chipset = "Qualcomm Snapdragon 680 4G (6nm)", androidVersion = "Android 14", ram = "4GB / 6GB", storage = "128GB", network = "4G LTE", battery = "5000 mAh", charging = "25W Fast Charging", notes = "Side-mounted fingerprint, 90Hz FHD+ LCD"),
                ModelEntity(brand = "Samsung", modelName = "Galaxy A15", modelNumber = "SM-A155F", chipset = "MediaTek Helio G99 (6nm)", androidVersion = "Android 14 (One UI 6.1)", ram = "6GB / 8GB", storage = "128GB / 256GB", network = "4G LTE", battery = "5000 mAh", charging = "25W Super Fast", notes = "Super AMOLED 90Hz 800 nits, Triple 50MP"),
                ModelEntity(brand = "Samsung", modelName = "Galaxy A24", modelNumber = "SM-A245F", chipset = "MediaTek Helio G99 (6nm)", androidVersion = "Android 14", ram = "6GB / 8GB", storage = "128GB", network = "4G LTE", battery = "5000 mAh", charging = "25W", notes = "Super AMOLED 90Hz, OIS 50MP camera"),
                ModelEntity(brand = "Samsung", modelName = "Galaxy A34", modelNumber = "SM-A346B", chipset = "MediaTek Dimensity 1080 (6nm)", androidVersion = "Android 14", ram = "6GB / 8GB", storage = "128GB / 256GB", network = "5G / 4G", battery = "5000 mAh", charging = "25W Fast Charging", notes = "Super AMOLED 120Hz, IP67 dust/water resistant"),
                ModelEntity(brand = "Samsung", modelName = "Galaxy A54", modelNumber = "SM-A546B", chipset = "Exynos 1380 (5nm)", androidVersion = "Android 14 (One UI 6.1)", ram = "8GB", storage = "128GB / 256GB", network = "5G / 4G", battery = "5000 mAh", charging = "25W Fast Charging", notes = "Glass back Gorilla Glass 5, IP67, OIS 50MP"),

                // Vivo
                ModelEntity(brand = "Vivo", modelName = "Vivo Y02", modelNumber = "V2217", chipset = "MediaTek Helio P22 (12nm)", androidVersion = "Android 12 Go (Funtouch 12)", ram = "2GB / 3GB", storage = "32GB", network = "4G LTE", battery = "5000 mAh", charging = "10W Standard", notes = "MicroUSB 2.0, IPS LCD 6.51 inches"),
                ModelEntity(brand = "Vivo", modelName = "Vivo Y03", modelNumber = "V2332", chipset = "MediaTek Helio G85 (12nm)", androidVersion = "Android 14 (Funtouch 14)", ram = "4GB", storage = "64GB / 128GB", network = "4G LTE", battery = "5000 mAh", charging = "15W FlashCharge", notes = "90Hz Sunlight display, IP54 dust and splash resistant"),
                ModelEntity(brand = "Vivo", modelName = "Vivo Y04", modelNumber = "V2348", chipset = "Unisoc T606 (12nm)", androidVersion = "Android 14 (Funtouch 14)", ram = "4GB", storage = "64GB / 128GB", network = "4G LTE", battery = "5000 mAh", charging = "15W Fast Charge", notes = "Type-C 2.0, Dual SIM, 13MP camera"),
                ModelEntity(brand = "Vivo", modelName = "Vivo Y18", modelNumber = "V2333", chipset = "MediaTek Helio G85 (12nm)", androidVersion = "Android 14", ram = "4GB / 8GB", storage = "64GB / 128GB", network = "4G LTE", battery = "5000 mAh", charging = "15W FlashCharge", notes = "90Hz High Brightness display (840 nits), 50MP ultra clear"),
                ModelEntity(brand = "Vivo", modelName = "Vivo Y21", modelNumber = "V2111", chipset = "MediaTek Helio P35 (12nm)", androidVersion = "Android 12", ram = "4GB", storage = "64GB / 128GB", network = "4G LTE", battery = "5000 mAh", charging = "18W Fast Charge", notes = "Side fingerprint, Type-C, Slim design 8.0mm"),
                ModelEntity(brand = "Vivo", modelName = "Vivo Y100A", modelNumber = "V2240", chipset = "Qualcomm Snapdragon 695 5G (6nm)", androidVersion = "Android 14 (Funtouch 14)", ram = "8GB", storage = "128GB / 256GB", network = "5G / 4G", battery = "4500 mAh", charging = "44W FlashCharge", notes = "Color changing Fluorite AG glass, AMOLED 90Hz"),

                // Xiaomi / Redmi / POCO
                ModelEntity(brand = "Xiaomi", modelName = "Redmi Note 13", modelNumber = "2312DRAABG", chipset = "MediaTek Dimensity 6080 (6nm)", androidVersion = "Android 14 (HyperOS)", ram = "6GB / 8GB", storage = "128GB / 256GB", network = "5G / 4G", battery = "5000 mAh", charging = "33W Turbo Charge", notes = "AMOLED 120Hz 1000 nits, 108MP camera"),
                ModelEntity(brand = "Redmi", modelName = "Redmi 12", modelNumber = "23053RN02Y", chipset = "MediaTek Helio G88 (12nm)", androidVersion = "Android 14 (HyperOS)", ram = "4GB / 8GB", storage = "128GB / 256GB", network = "4G LTE", battery = "5000 mAh", charging = "18W Fast Charge", notes = "Glass back design, 90Hz FHD+ display"),
                ModelEntity(brand = "POCO", modelName = "POCO M6 Pro", modelNumber = "2312FPCA6G", chipset = "MediaTek Helio G99 Ultra (6nm)", androidVersion = "Android 14 (HyperOS)", ram = "8GB / 12GB", storage = "256GB / 512GB", network = "4G LTE", battery = "5000 mAh", charging = "67W Turbo Charge", notes = "Flow AMOLED 120Hz, 64MP OIS camera"),

                // OPPO / Realme / OnePlus
                ModelEntity(brand = "OPPO", modelName = "OPPO A78", modelNumber = "CPH2565", chipset = "Qualcomm Snapdragon 680 (6nm)", androidVersion = "Android 14 (ColorOS 14)", ram = "8GB", storage = "128GB / 256GB", network = "4G LTE", battery = "5000 mAh", charging = "67W SUPERVOOC", notes = "AMOLED 90Hz, Dual stereo speakers"),
                ModelEntity(brand = "Realme", modelName = "Realme C53", modelNumber = "RMX3760", chipset = "Unisoc Tiger T612 (12nm)", androidVersion = "Android 13 (Realme UI T)", ram = "6GB", storage = "128GB", network = "4G LTE", battery = "5000 mAh", charging = "33W SUPERVOOC", notes = "90Hz display, Mini Capsule notification"),
                ModelEntity(brand = "OnePlus", modelName = "Nord CE 3 Lite", modelNumber = "CPH2493", chipset = "Qualcomm Snapdragon 695 5G (6nm)", androidVersion = "Android 14 (OxygenOS 14)", ram = "8GB", storage = "128GB / 256GB", network = "5G / 4G", battery = "5000 mAh", charging = "67W SUPERVOOC", notes = "120Hz display, 108MP camera"),

                // Tecno / Infinix / Itel
                ModelEntity(brand = "Tecno", modelName = "Spark 20 Pro", modelNumber = "KJ6", chipset = "MediaTek Helio G99 (6nm)", androidVersion = "Android 13 (HiOS 13.5)", ram = "8GB", storage = "256GB", network = "4G LTE", battery = "5000 mAh", charging = "33W Fast Charge", notes = "120Hz FHD+ IPS, 108MP main sensor"),
                ModelEntity(brand = "Infinix", modelName = "Hot 40 Pro", modelNumber = "X6837", chipset = "MediaTek Helio G99 (6nm)", androidVersion = "Android 13 (XOS 13.5)", ram = "8GB", storage = "128GB / 256GB", network = "4G LTE", battery = "5000 mAh", charging = "33W Fast Charge", notes = "Magic Ring notification, 120Hz display"),
                ModelEntity(brand = "Itel", modelName = "Itel S23+", modelNumber = "S665LN", chipset = "Unisoc Tiger T616 (12nm)", androidVersion = "Android 13 (itel OS 13)", ram = "8GB", storage = "256GB", network = "4G LTE", battery = "5000 mAh", charging = "18W Fast Charge", notes = "3D Curved AMOLED screen, In-display fingerprint"),

                // Huawei / Honor / Motorola / Nokia / IQOO
                ModelEntity(brand = "Huawei", modelName = "Nova 11i", modelNumber = "MAO-LX9", chipset = "Qualcomm Snapdragon 680 (6nm)", androidVersion = "EMUI 13", ram = "8GB", storage = "128GB", network = "4G LTE", battery = "5000 mAh", charging = "40W SuperCharge Turbo", notes = "90Hz bezel-less display, 48MP camera"),
                ModelEntity(brand = "Honor", modelName = "Honor X8b", modelNumber = "LLY-LX1", chipset = "Qualcomm Snapdragon 680 (6nm)", androidVersion = "Android 14 (MagicOS 8)", ram = "8GB", storage = "256GB / 512GB", network = "4G LTE", battery = "4500 mAh", charging = "35W SuperCharge", notes = "AMOLED 90Hz 2000 nits, 108MP camera, Magic Capsule"),
                ModelEntity(brand = "Motorola", modelName = "Moto G54", modelNumber = "XT2343-1", chipset = "MediaTek Dimensity 7020 (6nm)", androidVersion = "Android 14 (MyUX)", ram = "8GB / 12GB", storage = "128GB / 256GB", network = "5G / 4G", battery = "6000 mAh", charging = "33W TurboPower", notes = "120Hz FHD+, 50MP OIS camera"),
                ModelEntity(brand = "Nokia", modelName = "Nokia G42", modelNumber = "TA-1581", chipset = "Qualcomm Snapdragon 480+ 5G (8nm)", androidVersion = "Android 14", ram = "6GB", storage = "128GB", network = "5G / 4G", battery = "5000 mAh", charging = "20W Fast Charge", notes = "QuickFix repairability, 90Hz HD+ screen"),
                ModelEntity(brand = "IQOO", modelName = "iQOO Z9", modelNumber = "I2302", chipset = "MediaTek Dimensity 7200 (4nm)", androidVersion = "Android 14 (Funtouch 14)", ram = "8GB", storage = "128GB / 256GB", network = "5G / 4G", battery = "5000 mAh", charging = "44W FlashCharge", notes = "120Hz Ultra AMOLED, Sony IMX882 OIS")
            )
            modelDao.insertAll(initialModels)

            // Pre-seed Diagrams & Schematics
            val initialDiagrams = listOf(
                // Samsung Galaxy A235F
                DiagramEntity(
                    brand = "Samsung",
                    model = "Galaxy A235F",
                    diagramType = "Charging Diagram",
                    title = "Galaxy A235F Type-C & Fast Charging Sub-board Circuit",
                    description = "OVP Over-voltage protection IC, VBUS 5.0V/9.0V lines, CC1/CC2 lines, Thermistor TH1000, and PMIC charging control path.",
                    testPoints = "TP_VBUS: 5.0V / 9.0V, TP_VBAT: 3.85V-4.4V, TP_TH1000: 0.95V (NTC 10K)",
                    voltageSpecs = "VBUS_IN: 5.0V-9.0V 25W Max, CHG_OUT: 4.4V 3.0A, PMIC_VREG_BOOST: 5.0V"
                ),
                DiagramEntity(
                    brand = "Samsung",
                    model = "Galaxy A235F",
                    diagramType = "Power Section",
                    title = "Galaxy A235F PMIC S2MPU12 Power Distribution & Rails",
                    description = "Primary PMIC Power buck converters, LDO regulators, CPU Snapdragon 680 core voltage rails, and startup sequence.",
                    testPoints = "TP_VCORE: 0.85V, TP_VDRAM: 1.1V, TP_VIO18: 1.8V, TP_PWRKEY: 1.8V Active Low",
                    voltageSpecs = "BUCK1 (VCORE): 0.85V, BUCK2 (VGPU): 0.80V, LDO_VSRAM: 0.90V, VBAT_SYS: 3.8V"
                ),
                DiagramEntity(
                    brand = "Samsung",
                    model = "Galaxy A235F",
                    diagramType = "LCD Section",
                    title = "Galaxy A235F LCD Connector Pinout & Backlight Circuit",
                    description = "40-Pin FPC display connector, Backlight boost coil (28V DC), LED+ Anode / LED- Cathode lines, and MIPI DSI lane filters.",
                    testPoints = "TP_LEDA: +26.5V Boost, TP_LEDK: 0.6V PWM, TP_LCD_1V8: 1.8V, TP_LCD_3V0: 3.0V",
                    voltageSpecs = "VSP (Positive Bias): +5.5V, VSN (Negative Bias): -5.5V, AVDD: 6.0V"
                ),
                DiagramEntity(
                    brand = "Samsung",
                    model = "Galaxy A235F",
                    diagramType = "Network Section",
                    title = "Galaxy A235F RF Frontend & 4G LTE Transceiver Map",
                    description = "RF Power Amplifier, Antenna switch matrix, Band 1/3/5/8/40 filters, Transceiver WTR3925 and coaxial line trace.",
                    testPoints = "TP_RX_ANT: 50 Ohm matched, TP_PA_VCC: 3.4V, TP_RFFE_CLK: 1.8V, TP_RFFE_DATA: 1.8V",
                    voltageSpecs = "VREG_RF_1P8: 1.8V, VREG_RF_2P8: 2.8V, VPA_APT: 0.6V to 3.4V"
                ),

                // Vivo Y21
                DiagramEntity(
                    brand = "Vivo",
                    model = "Y21",
                    diagramType = "Charging Diagram",
                    title = "Vivo Y21 Type-C Sub-board & Charge IC Circuit",
                    description = "Charging sub-board schematic, OVP IC protection, VBUS 5.0V trace to MT6357 PMIC charge controller and battery detection pin.",
                    testPoints = "TP_VBUS: 5.12V, TP_VBAT: 3.85V-4.35V, TP_BAT_THERM: 0.90V, TP_ID: 1.8V Pullup",
                    voltageSpecs = "VBUS_IN: 5.0V ± 0.25V, CHG_OUT: 4.35V 2.0A Max, PMIC_VREG_BOOST: 5.0V"
                ),
                DiagramEntity(
                    brand = "Vivo",
                    model = "Y21",
                    diagramType = "Power Diagram",
                    title = "Vivo Y21 MediaTek Helio P35 & PMIC Power Architecture",
                    description = "MT6357 Power rail distribution, Buck converters (VCORE, VGPU, VMODEM), LDO regulators, and crystal oscillator lines.",
                    testPoints = "TP_CLK: 26MHz Sys Clock, TP_RST_N: 1.8V, TP_RTC: 32.768kHz, TP_VCORE: 0.85V",
                    voltageSpecs = "VDD_CORE: 0.85V, VDD_MEM: 1.2V, VDD_IO: 1.8V, VBAT_SYS: 3.8V"
                ),
                DiagramEntity(
                    brand = "Vivo",
                    model = "Y21",
                    diagramType = "LCD Diagram",
                    title = "Vivo Y21 LCD Interface & Backlight Driver Schematic",
                    description = "30-pin FPC connector layout, Backlight driver boost coil, MIPI differential pairs, and touch reset/interrupt lines.",
                    testPoints = "TP_LEDA: +25.0V, TP_LEDK: 0.5V PWM, TP_LCD_1V8: 1.8V, TP_TS_INT: 1.8V",
                    voltageSpecs = "VSP: +5.5V, VSN: -5.5V, VDD_TP: 2.8V"
                ),

                // OPPO A57
                DiagramEntity(
                    brand = "OPPO",
                    model = "A57",
                    diagramType = "Charging Diagram",
                    title = "OPPO A57 SUPERVOOC 33W Fast Charging Sub-board",
                    description = "SUPERVOOC 33W flash charging circuit, dual MOSFET protection switch, Type-C CC controller, and battery temp sensor.",
                    testPoints = "TP_VBUS: 5.0V / 11.0V (VOOC), TP_VBAT: 4.4V, TP_VOOC_DATA: Bi-directional sync",
                    voltageSpecs = "VBUS_VOOC: 11.0V 3.0A, VBAT_MAX: 4.45V, VREG_CHG_5V: 5.0V"
                ),
                DiagramEntity(
                    brand = "OPPO",
                    model = "A57",
                    diagramType = "Power Diagram",
                    title = "OPPO A57 Helio G35 / PMIC Power Rail & Boot Sequences",
                    description = "Power-on reset timing, MT6357 PMIC outputs, memory VDD lines, clock generator, and sleep mode rail behavior.",
                    testPoints = "TP_PWRKEY: 1.8V Active Low, TP_WATCHDOG: 1.8V, TP_VPROC: 0.85V, TP_VDD_RAM: 1.1V",
                    voltageSpecs = "VCORE: 0.85V, VPROC: 0.80V, VIO18: 1.8V, VSRAM: 0.90V"
                ),
                DiagramEntity(
                    brand = "OPPO",
                    model = "A57",
                    diagramType = "Network Diagram",
                    title = "OPPO A57 4G RF Front-End & Antenna Switch Map",
                    description = "Multiband PA module, RF transceiver I/Q signals, Low noise amplifier (LNA), and GPS/WiFi/Bluetooth co-existence filter.",
                    testPoints = "TP_ANT_MAIN: 50 Ohm matched, TP_PA_VCC: 3.4V, TP_RFFE_CLK: 1.8V, TP_RFFE_DATA: 1.8V",
                    voltageSpecs = "VREG_RF18: 1.8V, VREG_RF28: 2.8V, VPA_APT: 0.6V to 3.4V"
                ),

                // Vivo Y04
                DiagramEntity(
                    brand = "Vivo",
                    model = "Vivo Y04",
                    diagramType = "Charging Diagram",
                    title = "Vivo Y04 USB Type-C & OVP Sub-board Circuit",
                    description = "Charging sub-board schematic trace, OVP IC (Over Voltage Protection) pinout, VBUS line filtering, and D+/D- lines to PMIC PM6125.",
                    testPoints = "TP_VBUS: 5.12V (from USB), TP_VBAT: 3.85V-4.35V, TP_THERM: 0.95V (NTC 10K)",
                    voltageSpecs = "VBUS_IN: 5.0V ± 0.25V, CHG_OUT: 4.35V 2.1A Max, PMIC_VREG_BOOST: 5.0V"
                ),
                DiagramEntity(
                    brand = "Vivo",
                    model = "Vivo Y04",
                    diagramType = "Power Diagram",
                    title = "Vivo Y04 Main Board System Architecture & IC Map",
                    description = "Main logic board schematic with Unisoc T606 processor, eMMC 5.1 memory IC, PMU power lines, and RF front-end module layout.",
                    testPoints = "TP_CLK: 26MHz (Sys Clock), TP_RST_N: 1.8V (Reset Line), TP_PWRKEY: 1.8V Active Low",
                    voltageSpecs = "VDD_CORE: 0.85V, VDD_MEM: 1.2V, VDD_IO: 1.8V, VBAT_SYS: 3.8V"
                ),
                DiagramEntity(
                    brand = "Xiaomi",
                    model = "Redmi Note 13",
                    diagramType = "Network Diagram",
                    title = "Redmi Note 13 RF Transceiver & 5G Antenna Tuning",
                    description = "RF Frontend circuitry, Band 1/3/5/8/28/40/78 Power Amplifiers, Antenna switch matrix IC, and Baseband I/Q clock sync.",
                    testPoints = "TP_RX_ANT1: 50 Ohm matched, TP_PA_BIAS: 2.8V, TP_RFFE_DATA: 1.8V",
                    voltageSpecs = "VREG_RF_1P8: 1.8V, VREG_RF_3P0: 3.0V, VPA_APT: 0.6V to 3.4V"
                )
            )
            diagramDao.insertAll(initialDiagrams)

            // Pre-seed LCD Specifications with Groups
            val initialLcds = listOf(
                // ==========================================
                // VIVO
                // ==========================================
                // Vivo U1 / Y1S Series (9 models)
                LcdEntity(brand = "Vivo", groupName = "Vivo U1 / Y1S Series", model = "Vivo U1", modelCode = "1824", lcdName = "Vivo U1 Waterdrop IPS Display", lcdType = "IPS LCD", displaySize = "6.22 inch", resolution = "720 × 1520", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Capacitive Multi-touch", compatibleModels = "Vivo U1, Y1S, Y90, Y91, Y91i, Y91C, Y93, Y95, Y93s", price = 1350.0, stock = "In Stock", notes = "Universal 34-pin display for Vivo 6.22\" waterdrop series."),
                LcdEntity(brand = "Vivo", groupName = "Vivo U1 / Y1S Series", model = "Y1S", modelCode = "V2015", lcdName = "Vivo Y1S Display Assembly", lcdType = "IPS LCD", displaySize = "6.22 inch", resolution = "720 × 1520", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch Glass", compatibleModels = "Vivo U1, Y1S, Y90, Y91, Y91i, Y91C, Y93, Y95, Y93s", price = 1350.0, stock = "In Stock", notes = "Original grade replacement LCD."),
                LcdEntity(brand = "Vivo", groupName = "Vivo U1 / Y1S Series", model = "Y90", modelCode = "1908", lcdName = "Vivo Y90 Display Assembly", lcdType = "IPS LCD", displaySize = "6.22 inch", resolution = "720 × 1520", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo U1, Y1S, Y90, Y91, Y91i, Y91C, Y93, Y95, Y93s", price = 1300.0, stock = "In Stock", notes = "Fit with frame or standalone glass."),
                LcdEntity(brand = "Vivo", groupName = "Vivo U1 / Y1S Series", model = "Y91", modelCode = "1816", lcdName = "Vivo Y91 Display Assembly", lcdType = "IPS LCD", displaySize = "6.22 inch", resolution = "720 × 1520", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo U1, Y1S, Y90, Y91, Y91i, Y91C, Y93, Y95, Y93s", price = 1350.0, stock = "In Stock", notes = "High brightness backlight."),
                LcdEntity(brand = "Vivo", groupName = "Vivo U1 / Y1S Series", model = "Y91i", modelCode = "1820", lcdName = "Vivo Y91i Display Assembly", lcdType = "IPS LCD", displaySize = "6.22 inch", resolution = "720 × 1520", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo U1, Y1S, Y90, Y91, Y91i, Y91C, Y93, Y95, Y93s", price = 1350.0, stock = "In Stock", notes = "Universal fit."),
                LcdEntity(brand = "Vivo", groupName = "Vivo U1 / Y1S Series", model = "Y91C", modelCode = "1820", lcdName = "Vivo Y91C Display Assembly", lcdType = "IPS LCD", displaySize = "6.22 inch", resolution = "720 × 1520", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo U1, Y1S, Y90, Y91, Y91i, Y91C, Y93, Y95, Y93s", price = 1350.0, stock = "In Stock", notes = "Universal fit."),
                LcdEntity(brand = "Vivo", groupName = "Vivo U1 / Y1S Series", model = "Y93", modelCode = "1818", lcdName = "Vivo Y93 Display Assembly", lcdType = "IPS LCD", displaySize = "6.22 inch", resolution = "720 × 1520", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo U1, Y1S, Y90, Y91, Y91i, Y91C, Y93, Y95, Y93s", price = 1350.0, stock = "In Stock", notes = "Universal fit."),
                LcdEntity(brand = "Vivo", groupName = "Vivo U1 / Y1S Series", model = "Y95", modelCode = "1807", lcdName = "Vivo Y95 Display Assembly", lcdType = "IPS LCD", displaySize = "6.22 inch", resolution = "720 × 1520", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo U1, Y1S, Y90, Y91, Y91i, Y91C, Y93, Y95, Y93s", price = 1400.0, stock = "In Stock", notes = "Universal fit."),
                LcdEntity(brand = "Vivo", groupName = "Vivo U1 / Y1S Series", model = "Y93s", modelCode = "1819", lcdName = "Vivo Y93s Display Assembly", lcdType = "IPS LCD", displaySize = "6.22 inch", resolution = "720 × 1520", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo U1, Y1S, Y90, Y91, Y91i, Y91C, Y93, Y95, Y93s", price = 1400.0, stock = "In Stock", notes = "Universal fit."),

                // Vivo Y81 Series (5 models)
                LcdEntity(brand = "Vivo", groupName = "Vivo Y81 Series", model = "Vivo Y81", modelCode = "1726", lcdName = "Vivo Y81 Display Panel", lcdType = "IPS LCD", displaySize = "6.22 inch", resolution = "720 × 1520", connectorType = "Compatible LCD Connector 30-pin FPC", touchInfo = "Gorilla Glass Touch", compatibleModels = "Vivo Y81, Y81i, Y83s, Y83, Y83 Pro", price = 1300.0, stock = "In Stock", notes = "Tested before packaging."),
                LcdEntity(brand = "Vivo", groupName = "Vivo Y81 Series", model = "Y81i", modelCode = "1812", lcdName = "Vivo Y81i Display Panel", lcdType = "IPS LCD", displaySize = "6.22 inch", resolution = "720 × 1520", connectorType = "Compatible LCD Connector 30-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo Y81, Y81i, Y83s, Y83, Y83 Pro", price = 1300.0, stock = "In Stock", notes = "Compatible 30-pin series."),
                LcdEntity(brand = "Vivo", groupName = "Vivo Y81 Series", model = "Y83s", modelCode = "1805", lcdName = "Vivo Y83s Display Panel", lcdType = "IPS LCD", displaySize = "6.22 inch", resolution = "720 × 1520", connectorType = "Compatible LCD Connector 30-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo Y81, Y81i, Y83s, Y83, Y83 Pro", price = 1350.0, stock = "In Stock", notes = "Compatible 30-pin series."),
                LcdEntity(brand = "Vivo", groupName = "Vivo Y81 Series", model = "Y83", modelCode = "1802", lcdName = "Vivo Y83 Display Panel", lcdType = "IPS LCD", displaySize = "6.22 inch", resolution = "720 × 1520", connectorType = "Compatible LCD Connector 30-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo Y81, Y81i, Y83s, Y83, Y83 Pro", price = 1350.0, stock = "In Stock", notes = "Compatible 30-pin series."),
                LcdEntity(brand = "Vivo", groupName = "Vivo Y81 Series", model = "Y83 Pro", modelCode = "1808", lcdName = "Vivo Y83 Pro Display Panel", lcdType = "IPS LCD", displaySize = "6.22 inch", resolution = "720 × 1520", connectorType = "Compatible LCD Connector 30-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo Y81, Y81i, Y83s, Y83, Y83 Pro", price = 1400.0, stock = "In Stock", notes = "Compatible 30-pin series."),

                // Vivo V9 / Y85 Series (2 models)
                LcdEntity(brand = "Vivo", groupName = "Vivo V9 / Y85 Series", model = "Vivo V9 Youth", modelCode = "1727", lcdName = "Vivo V9 Youth Notch Display", lcdType = "IPS LCD", displaySize = "6.3 inch", resolution = "1080 × 2280", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo V9 Youth, Vivo Y85", price = 1500.0, stock = "In Stock", notes = "FHD+ Notch display assembly."),
                LcdEntity(brand = "Vivo", groupName = "Vivo V9 / Y85 Series", model = "Y85", modelCode = "1726", lcdName = "Vivo Y85 Display Assembly", lcdType = "IPS LCD", displaySize = "6.26 inch", resolution = "1080 × 2280", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo V9 Youth, Vivo Y85", price = 1500.0, stock = "In Stock", notes = "FHD+ Notch display assembly."),

                // Vivo Y11 / Y12 / Y15 / Y17 Series (7 models)
                LcdEntity(brand = "Vivo", groupName = "Vivo Y11 / Y12 / Y15 / Y17 Series", model = "Vivo Y11", modelCode = "1906", lcdName = "Vivo Y11 Display Screen", lcdType = "IPS LCD", displaySize = "6.35 inch", resolution = "720 × 1544", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo Y11, Y12, Y15, Y17, Y12s, Y12a, Y15s", price = 1400.0, stock = "In Stock", notes = "Halo FullView display."),
                LcdEntity(brand = "Vivo", groupName = "Vivo Y11 / Y12 / Y15 / Y17 Series", model = "Y12", modelCode = "1901", lcdName = "Vivo Y12 Display Screen", lcdType = "IPS LCD", displaySize = "6.35 inch", resolution = "720 × 1544", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo Y11, Y12, Y15, Y17, Y12s, Y12a, Y15s", price = 1400.0, stock = "In Stock", notes = "Halo FullView display."),
                LcdEntity(brand = "Vivo", groupName = "Vivo Y11 / Y12 / Y15 / Y17 Series", model = "Y15", modelCode = "1901", lcdName = "Vivo Y15 Display Screen", lcdType = "IPS LCD", displaySize = "6.35 inch", resolution = "720 × 1544", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo Y11, Y12, Y15, Y17, Y12s, Y12a, Y15s", price = 1400.0, stock = "In Stock", notes = "Halo FullView display."),
                LcdEntity(brand = "Vivo", groupName = "Vivo Y11 / Y12 / Y15 / Y17 Series", model = "Y17", modelCode = "1902", lcdName = "Vivo Y17 Display Screen", lcdType = "IPS LCD", displaySize = "6.35 inch", resolution = "720 × 1544", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo Y11, Y12, Y15, Y17, Y12s, Y12a, Y15s", price = 1450.0, stock = "In Stock", notes = "Halo FullView display."),
                LcdEntity(brand = "Vivo", groupName = "Vivo Y11 / Y12 / Y15 / Y17 Series", model = "Y12s", modelCode = "2026", lcdName = "Vivo Y12s Display Screen", lcdType = "IPS LCD", displaySize = "6.51 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 30-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo Y11, Y12, Y15, Y17, Y12s, Y12a, Y15s", price = 1400.0, stock = "In Stock", notes = "In-cell technology."),
                LcdEntity(brand = "Vivo", groupName = "Vivo Y11 / Y12 / Y15 / Y17 Series", model = "Y12a", modelCode = "2033", lcdName = "Vivo Y12a Display Screen", lcdType = "IPS LCD", displaySize = "6.51 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 30-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo Y11, Y12, Y15, Y17, Y12s, Y12a, Y15s", price = 1400.0, stock = "In Stock", notes = "In-cell technology."),
                LcdEntity(brand = "Vivo", groupName = "Vivo Y11 / Y12 / Y15 / Y17 Series", model = "Y15s", modelCode = "2120", lcdName = "Vivo Y15s Display Screen", lcdType = "IPS LCD", displaySize = "6.51 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 30-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Vivo Y11, Y12, Y15, Y17, Y12s, Y12a, Y15s", price = 1400.0, stock = "In Stock", notes = "In-cell technology."),

                // Vivo Y02 / Y03 / Y04 Series (4 models)
                LcdEntity(brand = "Vivo", groupName = "Vivo Y02 / Y03 / Y04 Series", model = "Vivo Y02", modelCode = "V2217", lcdName = "Vivo Y02 Screen Panel", lcdType = "IPS LCD", displaySize = "6.51 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 30-pin FPC", touchInfo = "Capacitive touch", compatibleModels = "Vivo Y02, Vivo Y03, Vivo Y04, Vivo Y18", price = 1350.0, stock = "In Stock", notes = "Original IPS."),
                LcdEntity(brand = "Vivo", groupName = "Vivo Y02 / Y03 / Y04 Series", model = "Vivo Y03", modelCode = "V2332", lcdName = "Vivo Y03 90Hz Screen", lcdType = "IPS LCD 90Hz", displaySize = "6.56 inch", resolution = "720 × 1612", connectorType = "Compatible LCD Connector 30-pin FPC", touchInfo = "90Hz Refresh rate", compatibleModels = "Vivo Y02, Vivo Y03, Vivo Y04, Vivo Y18", price = 1450.0, stock = "In Stock", notes = "High brightness 90Hz display."),
                LcdEntity(brand = "Vivo", groupName = "Vivo Y02 / Y03 / Y04 Series", model = "Vivo Y04", modelCode = "V2348", lcdName = "Vivo Y04 90Hz Display", lcdType = "IPS LCD 90Hz", displaySize = "6.56 inch", resolution = "720 × 1612", connectorType = "Compatible LCD Connector 30-pin FPC", touchInfo = "In-cell touch", compatibleModels = "Vivo Y02, Vivo Y03, Vivo Y04, Vivo Y18", price = 1450.0, stock = "In Stock", notes = "High brightness 90Hz display."),
                LcdEntity(brand = "Vivo", groupName = "Vivo Y02 / Y03 / Y04 Series", model = "Vivo Y18", modelCode = "V2333", lcdName = "Vivo Y18 90Hz Display", lcdType = "IPS LCD 90Hz", displaySize = "6.56 inch", resolution = "720 × 1612", connectorType = "Compatible LCD Connector 30-pin FPC", touchInfo = "90Hz High brightness", compatibleModels = "Vivo Y02, Vivo Y03, Vivo Y04, Vivo Y18", price = 1500.0, stock = "In Stock", notes = "High brightness 90Hz display."),

                // ==========================================
                // SAMSUNG
                // ==========================================
                // Samsung A12 / A02s Series (9 models)
                LcdEntity(brand = "Samsung", groupName = "Samsung A12 / A02s Series", model = "Galaxy A12", modelCode = "SM-A125F", lcdName = "Samsung A12 PLS Display", lcdType = "PLS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Galaxy A12, A02, A02s, M02, M02s, A12 Nacho, F02s, F12, M12", price = 1350.0, stock = "In Stock", notes = "Infinity-V PLS LCD."),
                LcdEntity(brand = "Samsung", groupName = "Samsung A12 / A02s Series", model = "Galaxy A02", modelCode = "SM-A022F", lcdName = "Samsung A02 PLS Display", lcdType = "PLS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Galaxy A12, A02, A02s, M02, M02s, A12 Nacho, F02s, F12, M12", price = 1300.0, stock = "In Stock", notes = "Compatible series."),
                LcdEntity(brand = "Samsung", groupName = "Samsung A12 / A02s Series", model = "Galaxy A02s", modelCode = "SM-A025F", lcdName = "Samsung A02s PLS Display", lcdType = "PLS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Galaxy A12, A02, A02s, M02, M02s, A12 Nacho, F02s, F12, M12", price = 1300.0, stock = "In Stock", notes = "Compatible series."),
                LcdEntity(brand = "Samsung", groupName = "Samsung A12 / A02s Series", model = "Galaxy M02", modelCode = "SM-M022G", lcdName = "Samsung M02 PLS Display", lcdType = "PLS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Galaxy A12, A02, A02s, M02, M02s, A12 Nacho, F02s, F12, M12", price = 1300.0, stock = "In Stock", notes = "Compatible series."),
                LcdEntity(brand = "Samsung", groupName = "Samsung A12 / A02s Series", model = "Galaxy M02s", modelCode = "SM-M025F", lcdName = "Samsung M02s PLS Display", lcdType = "PLS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Galaxy A12, A02, A02s, M02, M02s, A12 Nacho, F02s, F12, M12", price = 1300.0, stock = "In Stock", notes = "Compatible series."),
                LcdEntity(brand = "Samsung", groupName = "Samsung A12 / A02s Series", model = "Galaxy A12 Nacho", modelCode = "SM-A127F", lcdName = "Samsung A12 Nacho Display", lcdType = "PLS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Galaxy A12, A02, A02s, M02, M02s, A12 Nacho, F02s, F12, M12", price = 1350.0, stock = "In Stock", notes = "Compatible series."),
                LcdEntity(brand = "Samsung", groupName = "Samsung A12 / A02s Series", model = "Galaxy F02s", modelCode = "SM-E025F", lcdName = "Samsung F02s Display", lcdType = "PLS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Galaxy A12, A02, A02s, M02, M02s, A12 Nacho, F02s, F12, M12", price = 1300.0, stock = "In Stock", notes = "Compatible series."),
                LcdEntity(brand = "Samsung", groupName = "Samsung A12 / A02s Series", model = "Galaxy F12", modelCode = "SM-F127G", lcdName = "Samsung F12 90Hz Display", lcdType = "PLS LCD 90Hz", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "90Hz Refresh", compatibleModels = "Galaxy A12, A02, A02s, M02, M02s, A12 Nacho, F02s, F12, M12", price = 1400.0, stock = "In Stock", notes = "90Hz refresh rate."),
                LcdEntity(brand = "Samsung", groupName = "Samsung A12 / A02s Series", model = "Galaxy M12", modelCode = "SM-M127F", lcdName = "Samsung M12 90Hz Display", lcdType = "PLS LCD 90Hz", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "90Hz Refresh", compatibleModels = "Galaxy A12, A02, A02s, M02, M02s, A12 Nacho, F02s, F12, M12", price = 1400.0, stock = "In Stock", notes = "90Hz refresh rate."),

                // Samsung A50 / A30s Series (6 models)
                LcdEntity(brand = "Samsung", groupName = "Samsung A50 / A30s Series", model = "Galaxy A50", modelCode = "SM-A505F", lcdName = "Samsung A50 Super AMOLED", lcdType = "Super AMOLED", displaySize = "6.4 inch", resolution = "1080 × 2340", connectorType = "Compatible LCD Connector 40-pin FPC", touchInfo = "In-display Fingerprint", compatibleModels = "Galaxy A50, A30, A30s, A50s, A20, M30", price = 2900.0, stock = "In Stock", notes = "FHD+ Super AMOLED with In-display FP."),
                LcdEntity(brand = "Samsung", groupName = "Samsung A50 / A30s Series", model = "Galaxy A30", modelCode = "SM-A305F", lcdName = "Samsung A30 Super AMOLED", lcdType = "Super AMOLED", displaySize = "6.4 inch", resolution = "1080 × 2340", connectorType = "Compatible LCD Connector 40-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Galaxy A50, A30, A30s, A50s, A20, M30", price = 2700.0, stock = "In Stock", notes = "FHD+ Super AMOLED."),
                LcdEntity(brand = "Samsung", groupName = "Samsung A50 / A30s Series", model = "Galaxy A30s", modelCode = "SM-A307F", lcdName = "Samsung A30s Super AMOLED", lcdType = "Super AMOLED", displaySize = "6.4 inch", resolution = "720 × 1560", connectorType = "Compatible LCD Connector 40-pin FPC", touchInfo = "In-display Fingerprint", compatibleModels = "Galaxy A50, A30, A30s, A50s, A20, M30", price = 2800.0, stock = "In Stock", notes = "HD+ Super AMOLED with In-display FP."),
                LcdEntity(brand = "Samsung", groupName = "Samsung A50 / A30s Series", model = "Galaxy A50s", modelCode = "SM-A507F", lcdName = "Samsung A50s Super AMOLED", lcdType = "Super AMOLED", displaySize = "6.4 inch", resolution = "1080 × 2340", connectorType = "Compatible LCD Connector 40-pin FPC", touchInfo = "In-display Fingerprint", compatibleModels = "Galaxy A50, A30, A30s, A50s, A20, M30", price = 2900.0, stock = "In Stock", notes = "FHD+ Super AMOLED."),
                LcdEntity(brand = "Samsung", groupName = "Samsung A50 / A30s Series", model = "Galaxy A20", modelCode = "SM-A205F", lcdName = "Samsung A20 Super AMOLED", lcdType = "Super AMOLED", displaySize = "6.4 inch", resolution = "720 × 1560", connectorType = "Compatible LCD Connector 40-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Galaxy A50, A30, A30s, A50s, A20, M30", price = 2600.0, stock = "In Stock", notes = "HD+ Super AMOLED."),
                LcdEntity(brand = "Samsung", groupName = "Samsung A50 / A30s Series", model = "Galaxy M30", modelCode = "SM-M305F", lcdName = "Samsung M30 Super AMOLED", lcdType = "Super AMOLED", displaySize = "6.4 inch", resolution = "1080 × 2340", connectorType = "Compatible LCD Connector 40-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Galaxy A50, A30, A30s, A50s, A20, M30", price = 2700.0, stock = "In Stock", notes = "FHD+ Super AMOLED."),

                // ==========================================
                // OPPO
                // ==========================================
                // Oppo A5 / A9 2020 Series (5 models)
                LcdEntity(brand = "Oppo", groupName = "Oppo A5 / A9 2020 Series", model = "Oppo A5", modelCode = "CPH1931", lcdName = "Oppo A5 2020 Display Assembly", lcdType = "IPS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Gorilla Glass 3", compatibleModels = "Oppo A5, Oppo A9, Oppo A31, Oppo A11, Oppo A11x", price = 1350.0, stock = "In Stock", notes = "Waterdrop notch IPS LCD."),
                LcdEntity(brand = "Oppo", groupName = "Oppo A5 / A9 2020 Series", model = "Oppo A9", modelCode = "CPH1937", lcdName = "Oppo A9 2020 Display Assembly", lcdType = "IPS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Gorilla Glass 3", compatibleModels = "Oppo A5, Oppo A9, Oppo A31, Oppo A11, Oppo A11x", price = 1350.0, stock = "In Stock", notes = "Compatible series."),
                LcdEntity(brand = "Oppo", groupName = "Oppo A5 / A9 2020 Series", model = "Oppo A31", modelCode = "CPH2015", lcdName = "Oppo A31 Display Assembly", lcdType = "IPS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Gorilla Glass 3", compatibleModels = "Oppo A5, Oppo A9, Oppo A31, Oppo A11, Oppo A11x", price = 1350.0, stock = "In Stock", notes = "Compatible series."),
                LcdEntity(brand = "Oppo", groupName = "Oppo A5 / A9 2020 Series", model = "Oppo A11", modelCode = "PCHM10", lcdName = "Oppo A11 Display Assembly", lcdType = "IPS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Gorilla Glass 3", compatibleModels = "Oppo A5, Oppo A9, Oppo A31, Oppo A11, Oppo A11x", price = 1350.0, stock = "In Stock", notes = "Compatible series."),
                LcdEntity(brand = "Oppo", groupName = "Oppo A5 / A9 2020 Series", model = "Oppo A11x", modelCode = "PCHM30", lcdName = "Oppo A11x Display Assembly", lcdType = "IPS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Gorilla Glass 3", compatibleModels = "Oppo A5, Oppo A9, Oppo A31, Oppo A11, Oppo A11x", price = 1350.0, stock = "In Stock", notes = "Compatible series."),

                // Oppo F11 / F11 Pro Series (3 models)
                LcdEntity(brand = "Oppo", groupName = "Oppo F11 / F11 Pro Series", model = "Oppo F11", modelCode = "CPH1911", lcdName = "Oppo F11 Waterdrop LCD", lcdType = "LTPS IPS LCD", displaySize = "6.53 inch", resolution = "1080 × 2340", connectorType = "Compatible LCD Connector 40-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Oppo F11, Oppo F11 Pro, Oppo A9", price = 1600.0, stock = "In Stock", notes = "LTPS FHD+ resolution."),
                LcdEntity(brand = "Oppo", groupName = "Oppo F11 / F11 Pro Series", model = "Oppo F11 Pro", modelCode = "CPH1969", lcdName = "Oppo F11 Pro Full Display", lcdType = "LTPS IPS LCD", displaySize = "6.53 inch", resolution = "1080 × 2340", connectorType = "Compatible LCD Connector 40-pin FPC", touchInfo = "Panoramic Screen", compatibleModels = "Oppo F11, Oppo F11 Pro, Oppo A9", price = 1700.0, stock = "In Stock", notes = "No notch full screen for pop-up camera."),
                LcdEntity(brand = "Oppo", groupName = "Oppo F11 / F11 Pro Series", model = "Oppo A9 (2019)", modelCode = "PCAM10", lcdName = "Oppo A9 Display Panel", lcdType = "LTPS IPS LCD", displaySize = "6.53 inch", resolution = "1080 × 2340", connectorType = "Compatible LCD Connector 40-pin FPC", touchInfo = "Multi-touch", compatibleModels = "Oppo F11, Oppo F11 Pro, Oppo A9", price = 1600.0, stock = "In Stock", notes = "LTPS FHD+ resolution."),

                // ==========================================
                // XIAOMI / REDMI / POCO
                // ==========================================
                // Redmi Note 7 / 7 Pro Series (3 models)
                LcdEntity(brand = "Redmi", groupName = "Redmi Note 7 Series", model = "Redmi Note 7", modelCode = "M1901F7G", lcdName = "Redmi Note 7 Dot Drop Display", lcdType = "IPS LCD", displaySize = "6.3 inch", resolution = "1080 × 2340", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Corning Gorilla Glass 5", compatibleModels = "Redmi Note 7, Redmi Note 7 Pro, Redmi Note 7S", price = 1450.0, stock = "In Stock", notes = "FHD+ Dot Drop display with Gorilla Glass 5."),
                LcdEntity(brand = "Redmi", groupName = "Redmi Note 7 Series", model = "Redmi Note 7 Pro", modelCode = "M1901F7S", lcdName = "Redmi Note 7 Pro Display", lcdType = "IPS LCD", displaySize = "6.3 inch", resolution = "1080 × 2340", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Corning Gorilla Glass 5", compatibleModels = "Redmi Note 7, Redmi Note 7 Pro, Redmi Note 7S", price = 1450.0, stock = "In Stock", notes = "Compatible series."),
                LcdEntity(brand = "Redmi", groupName = "Redmi Note 7 Series", model = "Redmi Note 7S", modelCode = "M1901F7I", lcdName = "Redmi Note 7S Display", lcdType = "IPS LCD", displaySize = "6.3 inch", resolution = "1080 × 2340", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Corning Gorilla Glass 5", compatibleModels = "Redmi Note 7, Redmi Note 7 Pro, Redmi Note 7S", price = 1450.0, stock = "In Stock", notes = "Compatible series."),

                // Redmi Note 9 / 9S / 9 Pro Series (5 models)
                LcdEntity(brand = "Redmi", groupName = "Redmi Note 9 Series", model = "Redmi Note 9", modelCode = "M2003J15SC", lcdName = "Redmi Note 9 Punch-hole Screen", lcdType = "IPS LCD", displaySize = "6.53 inch", resolution = "1080 × 2340", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Corner punch hole", compatibleModels = "Redmi Note 9, Note 9S, Note 9 Pro, Note 9 Pro Max, POCO M2 Pro", price = 1550.0, stock = "In Stock", notes = "Left side corner punch hole."),
                LcdEntity(brand = "Redmi", groupName = "Redmi Note 9 Series", model = "Redmi Note 9S", modelCode = "M2003J6A1G", lcdName = "Redmi Note 9S DotDisplay", lcdType = "IPS LCD", displaySize = "6.67 inch", resolution = "1080 × 2400", connectorType = "Compatible LCD Connector 38-pin FPC", touchInfo = "Center punch hole", compatibleModels = "Redmi Note 9, Note 9S, Note 9 Pro, Note 9 Pro Max, POCO M2 Pro", price = 1650.0, stock = "In Stock", notes = "Center dot display."),
                LcdEntity(brand = "Redmi", groupName = "Redmi Note 9 Series", model = "Redmi Note 9 Pro", modelCode = "M2003J6B2G", lcdName = "Redmi Note 9 Pro DotDisplay", lcdType = "IPS LCD", displaySize = "6.67 inch", resolution = "1080 × 2400", connectorType = "Compatible LCD Connector 38-pin FPC", touchInfo = "Center punch hole", compatibleModels = "Redmi Note 9, Note 9S, Note 9 Pro, Note 9 Pro Max, POCO M2 Pro", price = 1650.0, stock = "In Stock", notes = "Center dot display."),
                LcdEntity(brand = "Redmi", groupName = "Redmi Note 9 Series", model = "Redmi Note 9 Pro Max", modelCode = "M2003J6I1I", lcdName = "Redmi Note 9 Pro Max DotDisplay", lcdType = "IPS LCD", displaySize = "6.67 inch", resolution = "1080 × 2400", connectorType = "Compatible LCD Connector 38-pin FPC", touchInfo = "Center punch hole", compatibleModels = "Redmi Note 9, Note 9S, Note 9 Pro, Note 9 Pro Max, POCO M2 Pro", price = 1650.0, stock = "In Stock", notes = "Center dot display."),
                LcdEntity(brand = "POCO", groupName = "Redmi Note 9 Series", model = "POCO M2 Pro", modelCode = "M2003J6CI", lcdName = "POCO M2 Pro DotDisplay", lcdType = "IPS LCD", displaySize = "6.67 inch", resolution = "1080 × 2400", connectorType = "Compatible LCD Connector 38-pin FPC", touchInfo = "Center punch hole", compatibleModels = "Redmi Note 9, Note 9S, Note 9 Pro, Note 9 Pro Max, POCO M2 Pro", price = 1650.0, stock = "In Stock", notes = "Center dot display."),

                // ==========================================
                // REALME
                // ==========================================
                // Realme 5 / 5i / C3 Series (6 models)
                LcdEntity(brand = "Realme", groupName = "Realme 5 / 5i / C3 Series", model = "Realme 5", modelCode = "RMX1911", lcdName = "Realme 5 Mini-drop Display", lcdType = "IPS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Gorilla Glass 3+", compatibleModels = "Realme 5, Realme 5i, Realme 5s, Realme C3, Realme Narzo 10A, Realme Narzo 20A", price = 1350.0, stock = "In Stock", notes = "Mini-drop full screen."),
                LcdEntity(brand = "Realme", groupName = "Realme 5 / 5i / C3 Series", model = "Realme 5i", modelCode = "RMX2030", lcdName = "Realme 5i Display Panel", lcdType = "IPS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Gorilla Glass 3+", compatibleModels = "Realme 5, Realme 5i, Realme 5s, Realme C3, Realme Narzo 10A, Realme Narzo 20A", price = 1350.0, stock = "In Stock", notes = "Universal fit."),
                LcdEntity(brand = "Realme", groupName = "Realme 5 / 5i / C3 Series", model = "Realme 5s", modelCode = "RMX1925", lcdName = "Realme 5s Display Panel", lcdType = "IPS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Gorilla Glass 3+", compatibleModels = "Realme 5, Realme 5i, Realme 5s, Realme C3, Realme Narzo 10A, Realme Narzo 20A", price = 1350.0, stock = "In Stock", notes = "Universal fit."),
                LcdEntity(brand = "Realme", groupName = "Realme 5 / 5i / C3 Series", model = "Realme C3", modelCode = "RMX2027", lcdName = "Realme C3 Display Panel", lcdType = "IPS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Gorilla Glass 3+", compatibleModels = "Realme 5, Realme 5i, Realme 5s, Realme C3, Realme Narzo 10A, Realme Narzo 20A", price = 1350.0, stock = "In Stock", notes = "Universal fit."),
                LcdEntity(brand = "Realme", groupName = "Realme 5 / 5i / C3 Series", model = "Realme Narzo 10A", modelCode = "RMX2020", lcdName = "Realme Narzo 10A Display", lcdType = "IPS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Gorilla Glass 3+", compatibleModels = "Realme 5, Realme 5i, Realme 5s, Realme C3, Realme Narzo 10A, Realme Narzo 20A", price = 1350.0, stock = "In Stock", notes = "Universal fit."),
                LcdEntity(brand = "Realme", groupName = "Realme 5 / 5i / C3 Series", model = "Realme Narzo 20A", modelCode = "RMX2050", lcdName = "Realme Narzo 20A Display", lcdType = "IPS LCD", displaySize = "6.5 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "Gorilla Glass 3+", compatibleModels = "Realme 5, Realme 5i, Realme 5s, Realme C3, Realme Narzo 10A, Realme Narzo 20A", price = 1350.0, stock = "In Stock", notes = "Universal fit."),

                // Realme C53 / C51 / Narzo N53 Series (4 models)
                LcdEntity(brand = "Realme", groupName = "Realme C53 / C51 Series", model = "Realme C53", modelCode = "RMX3760", lcdName = "Realme C53 90Hz Display", lcdType = "IPS LCD 90Hz", displaySize = "6.74 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 38-pin FPC", touchInfo = "90Hz Refresh rate", compatibleModels = "Realme C53, Realme C51, Realme C55, Realme Narzo N53", price = 1400.0, stock = "In Stock", notes = "Universal 38-pin connector across C51/C53/Narzo series."),
                LcdEntity(brand = "Realme", groupName = "Realme C53 / C51 Series", model = "Realme C51", modelCode = "RMX3830", lcdName = "Realme C51 90Hz Display", lcdType = "IPS LCD 90Hz", displaySize = "6.74 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 38-pin FPC", touchInfo = "90Hz Refresh rate", compatibleModels = "Realme C53, Realme C51, Realme C55, Realme Narzo N53", price = 1400.0, stock = "In Stock", notes = "Universal 38-pin connector."),
                LcdEntity(brand = "Realme", groupName = "Realme C53 / C51 Series", model = "Realme C55", modelCode = "RMX3710", lcdName = "Realme C55 FHD+ 90Hz Display", lcdType = "IPS LCD 90Hz", displaySize = "6.72 inch", resolution = "1080 × 2400", connectorType = "Compatible LCD Connector 38-pin FPC", touchInfo = "90Hz Punch-hole", compatibleModels = "Realme C53, Realme C51, Realme C55, Realme Narzo N53", price = 1600.0, stock = "In Stock", notes = "FHD+ 90Hz punch hole."),
                LcdEntity(brand = "Realme", groupName = "Realme C53 / C51 Series", model = "Realme Narzo N53", modelCode = "RMX3761", lcdName = "Realme Narzo N53 Display", lcdType = "IPS LCD 90Hz", displaySize = "6.74 inch", resolution = "720 × 1600", connectorType = "Compatible LCD Connector 38-pin FPC", touchInfo = "90Hz Refresh rate", compatibleModels = "Realme C53, Realme C51, Realme C55, Realme Narzo N53", price = 1400.0, stock = "In Stock", notes = "Universal 38-pin connector."),

                // ==========================================
                // ONEPLUS
                // ==========================================
                // OnePlus Nord CE Series (4 models)
                LcdEntity(brand = "OnePlus", groupName = "OnePlus Nord CE Series", model = "Nord CE", modelCode = "EB2101", lcdName = "OnePlus Nord CE Fluid AMOLED", lcdType = "Fluid AMOLED 90Hz", displaySize = "6.43 inch", resolution = "1080 × 2400", connectorType = "Compatible LCD Connector 40-pin FPC", touchInfo = "In-display Fingerprint", compatibleModels = "Nord CE, Nord CE 2, Nord CE 2 Lite, Nord CE 3 Lite", price = 3600.0, stock = "In Stock", notes = "Fluid AMOLED with Under-display FP."),
                LcdEntity(brand = "OnePlus", groupName = "OnePlus Nord CE Series", model = "Nord CE 2", modelCode = "IV2201", lcdName = "OnePlus Nord CE 2 AMOLED", lcdType = "Fluid AMOLED 90Hz", displaySize = "6.43 inch", resolution = "1080 × 2400", connectorType = "Compatible LCD Connector 40-pin FPC", touchInfo = "In-display Fingerprint", compatibleModels = "Nord CE, Nord CE 2, Nord CE 2 Lite, Nord CE 3 Lite", price = 3800.0, stock = "In Stock", notes = "HDR10+ 90Hz AMOLED."),
                LcdEntity(brand = "OnePlus", groupName = "OnePlus Nord CE Series", model = "Nord CE 2 Lite", modelCode = "CPH2381", lcdName = "OnePlus Nord CE 2 Lite 120Hz", lcdType = "IPS LCD 120Hz", displaySize = "6.59 inch", resolution = "1080 × 2412", connectorType = "Compatible LCD Connector 38-pin FPC", touchInfo = "120Hz Refresh", compatibleModels = "Nord CE, Nord CE 2, Nord CE 2 Lite, Nord CE 3 Lite", price = 1800.0, stock = "In Stock", notes = "120Hz smooth IPS LCD."),
                LcdEntity(brand = "OnePlus", groupName = "OnePlus Nord CE Series", model = "Nord CE 3 Lite", modelCode = "CPH2493", lcdName = "OnePlus Nord CE 3 Lite 120Hz", lcdType = "IPS LCD 120Hz", displaySize = "6.72 inch", resolution = "1080 × 2400", connectorType = "Compatible LCD Connector 38-pin FPC", touchInfo = "120Hz 680 nits", compatibleModels = "Nord CE, Nord CE 2, Nord CE 2 Lite, Nord CE 3 Lite", price = 1900.0, stock = "In Stock", notes = "120Hz adaptive refresh display."),

                // ==========================================
                // TECNO / INFINIX / ITEL
                // ==========================================
                // Tecno Spark 20 / Hot 40 Series (5 models)
                LcdEntity(brand = "Tecno", groupName = "Tecno Spark 20 / Hot 40 Series", model = "Spark 20", modelCode = "KJ5", lcdName = "Tecno Spark 20 90Hz Display", lcdType = "IPS LCD 90Hz", displaySize = "6.56 inch", resolution = "720 × 1612", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "90Hz Magic Ring", compatibleModels = "Spark 20, Spark 20 Pro, Infinix Hot 40, Hot 40 Pro, Hot 40i", price = 1350.0, stock = "In Stock", notes = "Dynamic punch hole display."),
                LcdEntity(brand = "Tecno", groupName = "Tecno Spark 20 / Hot 40 Series", model = "Spark 20 Pro", modelCode = "KJ6", lcdName = "Tecno Spark 20 Pro 120Hz", lcdType = "IPS LCD 120Hz", displaySize = "6.78 inch", resolution = "1080 × 2460", connectorType = "Compatible LCD Connector 38-pin FPC", touchInfo = "120Hz FHD+", compatibleModels = "Spark 20, Spark 20 Pro, Infinix Hot 40, Hot 40 Pro, Hot 40i", price = 1650.0, stock = "In Stock", notes = "120Hz FHD+ smooth screen."),
                LcdEntity(brand = "Infinix", groupName = "Tecno Spark 20 / Hot 40 Series", model = "Infinix Hot 40", modelCode = "X6836", lcdName = "Infinix Hot 40 90Hz Screen", lcdType = "IPS LCD 90Hz", displaySize = "6.78 inch", resolution = "1080 × 2460", connectorType = "Compatible LCD Connector 38-pin FPC", touchInfo = "90Hz Magic Ring", compatibleModels = "Spark 20, Spark 20 Pro, Infinix Hot 40, Hot 40 Pro, Hot 40i", price = 1550.0, stock = "In Stock", notes = "Magic Ring notification display."),
                LcdEntity(brand = "Infinix", groupName = "Tecno Spark 20 / Hot 40 Series", model = "Infinix Hot 40 Pro", modelCode = "X6837", lcdName = "Infinix Hot 40 Pro 120Hz", lcdType = "IPS LCD 120Hz", displaySize = "6.78 inch", resolution = "1080 × 2460", connectorType = "Compatible LCD Connector 38-pin FPC", touchInfo = "120Hz Touch", compatibleModels = "Spark 20, Spark 20 Pro, Infinix Hot 40, Hot 40 Pro, Hot 40i", price = 1650.0, stock = "In Stock", notes = "120Hz FHD+ smooth screen."),
                LcdEntity(brand = "Infinix", groupName = "Tecno Spark 20 / Hot 40 Series", model = "Infinix Hot 40i", modelCode = "X6528B", lcdName = "Infinix Hot 40i 90Hz Screen", lcdType = "IPS LCD 90Hz", displaySize = "6.56 inch", resolution = "720 × 1612", connectorType = "Compatible LCD Connector 34-pin FPC", touchInfo = "90Hz Refresh", compatibleModels = "Spark 20, Spark 20 Pro, Infinix Hot 40, Hot 40 Pro, Hot 40i", price = 1300.0, stock = "In Stock", notes = "90Hz punch hole screen.")
            )
            lcdDao.insertAll(initialLcds)

            // Pre-seed Service Files
            val initialFiles = listOf(
                FileEntity(
                    fileName = "Vivo_Y04_V2348_PD2348F_EX_A_14.0.5.1_Flash_File.zip",
                    brand = "Vivo",
                    model = "Vivo Y04",
                    androidVersion = "Android 14",
                    fileType = "Flash File",
                    filePath = "/system/files/firmware/vivo_y04_flash_file.zip",
                    fileSize = "4.85 GB",
                    version = "PD2348F_EX_A_14.0.5.1",
                    description = "Official Scatter/PAC firmware for dead boot recovery, unbrick, and factory restoration via SP Flash / Unisoc Tool."
                ),
                FileEntity(
                    fileName = "Vivo_Y04_FRP_Bypass_OneClick_2026.bin",
                    brand = "Vivo",
                    model = "Vivo Y04",
                    androidVersion = "Android 14",
                    fileType = "Auth File",
                    filePath = "/system/files/auth/vivo_y04_frp_auth.bin",
                    fileSize = "18.4 MB",
                    version = "v3.2",
                    description = "FRP erase DA & Auth loader for MTK/Unisoc bypass with testpoint EDL method."
                ),
                FileEntity(
                    fileName = "Samsung_A15_SM-A155F_Binary4_Android14_Repair_Firmware.tar",
                    brand = "Samsung",
                    model = "Galaxy A15",
                    androidVersion = "Android 14",
                    fileType = "Firmware",
                    filePath = "/system/files/firmware/samsung_a15_b4.tar",
                    fileSize = "6.12 GB",
                    version = "A155FXXU4BXF1",
                    description = "4-Files Repair ROM (AP, BL, CP, CSC) for Odin 3.14.4 to fix bootloop and IMEI unknown issue."
                ),
                FileEntity(
                    fileName = "Redmi_Note_13_ENG_ROM_Factory_Test.zip",
                    brand = "Xiaomi",
                    model = "Redmi Note 13",
                    androidVersion = "Android 14",
                    fileType = "ENG ROM",
                    filePath = "/system/files/eng_rom/redmi_note13_eng.zip",
                    fileSize = "3.20 GB",
                    version = "ENG_v1.0_2026",
                    description = "Engineering ROM for baseband repair, hardware diagnostic calibration, and dual IMEI restore."
                ),
                FileEntity(
                    fileName = "MTK_Universal_Bypass_Tool_v5.8_Installer.exe",
                    brand = "Universal",
                    model = "All MTK Devices",
                    androidVersion = "All Androids",
                    fileType = "Tool",
                    filePath = "/system/files/tools/mtk_bypass_tool_v5.8.exe",
                    fileSize = "142.5 MB",
                    version = "v5.8.2",
                    description = "MediaTek Brom Exploit Tool to disable auth protection for SP Flash Tool and UnlockTool."
                ),
                FileEntity(
                    fileName = "Qualcomm_HS-USB_QDLoader_9008_Driver_64Bit.zip",
                    brand = "Universal",
                    model = "Snapdragon Devices",
                    androidVersion = "Windows 10/11",
                    fileType = "Driver",
                    filePath = "/system/files/drivers/qualcomm_9008_driver.zip",
                    fileSize = "24.6 MB",
                    version = "v2.1.3.8",
                    description = "Digitally signed EDL 9008 USB COM port drivers for unbricking Qualcomm devices."
                ),
                FileEntity(
                    fileName = "Vivo_Y18_Dump_EMMC_Full_Dump_Backup_32GB.rar",
                    brand = "Vivo",
                    model = "Vivo Y18",
                    androidVersion = "Android 14",
                    fileType = "Dump File",
                    filePath = "/system/files/dump/vivo_y18_emmc_dump.rar",
                    fileSize = "1.85 GB",
                    version = "v1.0",
                    description = "EasyJTAG / UFI Box raw read dump (ROM1, ROM2, ROM3, EXT_CSD) for eMMC replacement."
                )
            )
            fileDao.insertAll(initialFiles)

            // Pre-seed Sample Customers for testing dashboard & delivery reminders
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            val calendar = Calendar.getInstance()

            val todayDate = dateFormat.format(calendar.time)

            // Delivery time today 5:00 PM
            calendar.set(Calendar.HOUR_OF_DAY, 17)
            calendar.set(Calendar.MINUTE, 0)
            val todayDeliveryTimestamp = calendar.timeInMillis

            val customer1 = CustomerEntity(
                customerIdCode = "CUST-1001",
                password = "123456",
                isBlocked = false,
                customerName = "Rahim",
                mobileNumber = "01712345678",
                gmail = "rahim.mobile@gmail.com",
                address = "Shop 14, Central Market, Dhaka",
                brand = "Vivo",
                model = "Vivo Y04",
                imei = "864521049281723",
                serviceType = "Dead Boot",
                problemDescription = "Phone suddenly went dead after software update attempt. Power supply shows 0.08A constant current draw. Requires test point BROM flash and PMIC check.",
                serviceNote = "BROM Test point EDL flashed with SP Flash tool. PMIC rails verified at 0.85V.",
                serviceCharge = 1200.0,
                advancePayment = 400.0,
                dueAmount = 800.0,
                deliveryDate = todayDate,
                deliveryTime = "05:00 PM",
                deliveryTimestamp = todayDeliveryTimestamp,
                status = "Processing",
                voiceFilePath = null,
                voiceDurationMs = 0L,
                createdAt = System.currentTimeMillis() - 86400000L
            )

            val customer2 = CustomerEntity(
                customerIdCode = "CUST-1002",
                password = "123456",
                isBlocked = false,
                customerName = "Kamal Hossain",
                mobileNumber = "01898765432",
                gmail = "kamal.tech@yahoo.com",
                address = "Mirpur-10, Dhaka",
                brand = "Samsung",
                model = "Galaxy A15",
                imei = "359128058291048",
                serviceType = "Display",
                problemDescription = "Screen cracked after drop. Touch works on upper half only. Needs original Super AMOLED replacement with frame.",
                serviceNote = "Awaiting original AMOLED display part arrival.",
                serviceCharge = 3500.0,
                advancePayment = 2000.0,
                dueAmount = 1500.0,
                deliveryDate = todayDate,
                deliveryTime = "06:30 PM",
                deliveryTimestamp = todayDeliveryTimestamp + 5400000L,
                status = "Pending",
                voiceFilePath = null,
                voiceDurationMs = 0L,
                createdAt = System.currentTimeMillis() - 43200000L
            )

            val customer3 = CustomerEntity(
                customerIdCode = "CUST-1003",
                password = "123456",
                isBlocked = false,
                customerName = "Tanvir Ahmed",
                mobileNumber = "01911223344",
                gmail = "tanvir.ahmed@gmail.com",
                address = "Uttara Sector 7, Dhaka",
                brand = "Xiaomi",
                model = "Redmi Note 13",
                imei = "862340051928374",
                serviceType = "FRP",
                problemDescription = "Customer forgot Google account credentials after hard reset. FRP bypass required.",
                serviceNote = "FRP bypass completed via fastboot unlock and EDL auth.",
                serviceCharge = 800.0,
                advancePayment = 800.0,
                dueAmount = 0.0,
                deliveryDate = todayDate,
                deliveryTime = "03:00 PM",
                deliveryTimestamp = todayDeliveryTimestamp - 7200000L,
                status = "Completed",
                voiceFilePath = null,
                voiceDurationMs = 0L,
                createdAt = System.currentTimeMillis() - 25000000L
            )

            val customer4 = CustomerEntity(
                customerIdCode = "CUST-1004",
                password = "123456",
                isBlocked = false,
                customerName = "Sabbir Hossain",
                mobileNumber = "01655443322",
                gmail = "sabbir.hossain@gmail.com",
                address = "Dhanmondi 27, Dhaka",
                brand = "Vivo",
                model = "Vivo Y18",
                imei = "860492817293841",
                serviceType = "Charging",
                problemDescription = "Sub-board Type-C connector pin damaged, slow charging and OTG not detected.",
                serviceNote = "Replaced Type-C charging sub-board, fast charging OK.",
                serviceCharge = 650.0,
                advancePayment = 200.0,
                dueAmount = 450.0,
                deliveryDate = todayDate,
                deliveryTime = "07:00 PM",
                deliveryTimestamp = todayDeliveryTimestamp + 7200000L,
                status = "Pending",
                voiceFilePath = null,
                voiceDurationMs = 0L,
                createdAt = System.currentTimeMillis() - 15000000L
            )

            customerDao.insertAll(listOf(customer1, customer2, customer3, customer4))
        }
    }
}
