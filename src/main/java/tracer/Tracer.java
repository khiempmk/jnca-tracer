package tracer;

import lombok.extern.slf4j.Slf4j;
import tracer.conf.ConfigurationLoader;
import tracer.model.Node;
import tracer.utils.SensorUtility;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

@Slf4j
public class Tracer {
    private static String input_map = ConfigurationLoader.getInstance().getAsString("input_map_file","D:\\Code\\Desktop\\jnca_review\\data\\hop\\test10.INP");
    private static String input_ccs = ConfigurationLoader.getInstance().getAsString("input_ccs_file","D:\\Code\\Desktop\\jnca_review\\data\\hop\\NDST_test10.txt");
    public static List<Node> sensors = new ArrayList<>();
    public static List<Node> targets = new ArrayList<>();
    public static List<Node> sinks = new ArrayList<>();
    public static Queue<List<List<Integer>>> ccs_queue = new LinkedList<>();
    public static Queue<Double> ccs_life_time = new LinkedList<>();
    private static int numSensor , numTarget, numSink , ccs_size;
    private static double eMax,eMin, rSense , rCom ;
    public static double [] energy;
    public static double [] distanceToSink ;
    public static double lifeTime =0;
    public static List<List<Integer>> ccs_list = new ArrayList<>();
    public static  double average_hop = 0;
    public static  int max_hop = - Integer.MAX_VALUE ;
    public static double []dentaE ;
    public static void main(String[] args) {
        // Đọc dữ liệu từ file input và output của thuật toán
        init();
        // Tạo mảng khoảng cách gần nhất từ mỗi sensor đến sink
        getDistanceToSink();

        energy = new double[numSensor];
        dentaE = new double[numSensor];
        for (int i = 0 ; i < numSensor ; i++) {
            energy[i] = eMax;
            dentaE[i] = 0;
        }
        while (loadCCS()) ;
        log.info("Max hop : {} " ,max_hop);
        log.info("Average hop : {}", average_hop);
    }

    public static void init(){
        try {
            log.info("Initing ...");
            SensorUtility.readFile(input_map);
            SensorUtility.readCCS(input_ccs);
            ccs_size = SensorUtility.ccs_size ;
            numSensor = SensorUtility.mListSensorNodes.size();
            numTarget = SensorUtility.mListTargetNodes.size();
            numSink = SensorUtility.mListSinkNodes.size();
            eMax = SensorUtility.mEoValue;
            eMin =  0;
            rCom = SensorUtility.mRcValue  ;
            rSense= SensorUtility.mRsValue ;
            sensors.addAll(SensorUtility.mListSensorNodes) ;
            targets.addAll(SensorUtility.mListTargetNodes) ;
            sinks.addAll(SensorUtility.mListSinkNodes);
            ccs_queue.addAll(SensorUtility.ccs_list);
            ccs_life_time.addAll(SensorUtility.ccs_life_time);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    private static boolean equal( List<Integer> a, List<Integer> b){
        if (a.size() != b.size()) return  false ;
        for (int i = 0 ; i < a.size(); i++){
            if (!a.get(i).equals(b.get(i)))
                return false;
        }
        return true;
    }
    private static List<List<Integer>> sort_and_suffer(List<List<Integer>> ccs_list){
        Collections.sort(ccs_list, (o1, o2) -> {
            if (o1.size() < o2.size()) return -1 ;
            if (o1.size() > o2.size()) return  1 ;
            for (int i = 0 ; i < o1.size() ; i++){
                if (o1.get(i) < o2.get(i)) return -1 ;
                if (o1.get(i) > o2.get(i)) return  1 ;
            }
            return 0;
        });

        List<List<Integer>> output = new ArrayList<>();
        output.add(ccs_list.get(0)) ;
        for (int i = 1 ; i < ccs_list.size() ; i++){
            if (!equal(ccs_list.get(i), ccs_list.get(i-1)))
                output.add(ccs_list.get(i));
        }
        return output ;
    }
    public static boolean loadCCS(){
        try {
            if (ccs_queue.isEmpty()) return false;
            ccs_list = ccs_queue.poll();

            // Loại bỏ những ccp trùng nhau trong 1 ccs
            ccs_list = sort_and_suffer(ccs_list);
            log.info("Loading CCS, size {} ",ccs_list.size());
            double life_time_ccs = ccs_life_time.poll();
            lifeTime += life_time_ccs;
            for (List<Integer> ccp : ccs_list ){
                int num_hop = ccp.size() ;
                max_hop = Math.max(max_hop, num_hop);
                average_hop +=  (double) (num_hop)/ (ccs_size * ccs_list.size()) ;
            }

            for (int i = 0 ; i < numSensor; i++)
                dentaE[i] = 0 ;
            for (List<Integer> ccp : ccs_list){
                getEnergyConsumer(ccp);
            }
//            for (int i = 0; i < ccs_list.size() ; i++){
//                List<Integer> ccp = ccs_list.get(i);
//                int u = ccp.get(0);
//                double distance ;
//                if (ccp.size() >=2 ) {
//                    int v = ccp.get(1);
//                    distance = getDistance(sensors.get(u), sensors.get(v));
//                } else distance = distanceToSink[u];
//
//                dentaE[u] += getDentaE(distance,"sensing");
//
//                for (int j = 1; j < ccp.size()-1 ; j ++){
//                     u = ccp.get(j);
//                     int v = ccp.get(j+1);
//                     distance = getDistance(sensors.get(u), sensors.get(v));
//                     dentaE[u] += getDentaE(distance, "comm");
//                }
//                if (ccp.size() > 1) {
//                    int lastSensor = ccp.get(ccp.size() - 1);
//                    distance = distanceToSink[lastSensor];
//                    dentaE[lastSensor] += getDentaE(distance, "comm");
//                    if (distanceToSink[lastSensor] > rCom){
//                        log.error("distance to sink is over communication range");
//                    }
//                }
//            }

            // Tính lại năng lượng từng sensor sau ccs life time
            for (int i = 0 ; i < numSensor ; i++){
                    energy[i] -= dentaE[i] * life_time_ccs ;
                    if (energy[i] < 0 ){
                        log.error("Sensor {} died !! Something wrong !! energy {}", i, energy[i]);
//                        return false ;
                    }
                    if (energy[i] == 0) {
                        log.info("Sensor {} is out of energy", i);
                    }
            }
            File output_remain_energy = new File("D:\\Code\\Desktop\\jnca_review\\energy_out");
            try {
                FileWriter fileWriter = new FileWriter(output_remain_energy, true);
                for (int i = 0 ; i < numSensor; i++) {
                    fileWriter.write(energy[i] + "\t" );

                }
                fileWriter.write("\n");
                fileWriter.flush();
            } catch (Exception e){
                log.error(e.getMessage(),e);
            }
            return  true ;
        } catch (Exception e){
            return false;
        }
    }
    private static void getDistanceToSink(){
        distanceToSink = new double[numSensor];
        for (int i = 0 ; i < numSensor ; i++)
            distanceToSink[i] = Double.MAX_VALUE ;
        for (int i = 0 ; i < numSensor ; i++){
                Node sensor = sensors.get(i);
                for (int j = 0 ; j < numSink ; j++){
                    Node sink = sinks.get(j);
                    distanceToSink[i] = Math.min(distanceToSink[i], getDistance(sensor, sink)) ;

                }
        }
    }
    public static double getDistance(Node x, Node y ){
        return Math.sqrt(Math.pow(x.getXCoord() - y.getXCoord(),2) + Math.pow(x.getYCoord() - y.getYCoord(),2)) ;
    }
    private static double getDentaE(double d, String type){
        double r = SensorUtility.mBitValue ; //ConfigurationLoader.getInstance().getAsDouble("mBitValue", 16.0f);
        double er= SensorUtility.mErValue ; //ConfigurationLoader.getInstance().getAsDouble("mErValue",100.0);
        double es= SensorUtility.mEsValue ; // ConfigurationLoader.getInstance().getAsDouble("mEsValue", 100.0) ;
        double emp=SensorUtility.mEmpValue ;//ConfigurationLoader.getInstance().getAsDouble("mEmpValue",0.0000013) ;
        double efs=SensorUtility.mEfsValue ;//ConfigurationLoader.getInstance().getAsDouble("mEfsValue",0.01) ;
        double et= SensorUtility.mEtValue ;//ConfigurationLoader.getInstance().getAsDouble("mEtValue",50.0);
        double d0 = Math.sqrt(efs/emp);;
        if (type.equals("sensing")){
            if (d < d0)
                return r * es + r * (et + efs * Math.pow(d,2));
            else
                return r * es + r * (et + emp * Math.pow(d,4));
        } else {
            if (d < d0)
                return  r * er + r *( et + efs*( Math.pow(d,2)));
            else
                return  r * er + r * (et + emp*( Math.pow(d,4)));
        }
    }
    private static double bit = SensorUtility.mBitValue ; //ConfigurationLoader.getInstance().getAsDouble("mBitValue", 16.0f);
    private static double Er= SensorUtility.mErValue ; //ConfigurationLoader.getInstance().getAsDouble("mErValue",100.0);
    private static double Es= SensorUtility.mEsValue ; // ConfigurationLoader.getInstance().getAsDouble("mEsValue", 100.0) ;
    private static double Emp=SensorUtility.mEmpValue ;//ConfigurationLoader.getInstance().getAsDouble("mEmpValue",0.0000013) ;
    private static double Efs=SensorUtility.mEfsValue ;//ConfigurationLoader.getInstance().getAsDouble("mEfsValue",0.01) ;
    private static double Et= SensorUtility.mEtValue ;//ConfigurationLoader.getInstance().getAsDouble("mEtValue",50.0);
    private static double Do = Math.sqrt(Efs/Emp);
    private static void getEnergyConsumer(List<Integer> pathYi) {
            for (int i = 0; i < pathYi.size(); i++) {
            int sensor = pathYi.get(i);
            if (i == 0 ) {
                dentaE[sensor] += bit * Es;
                if (pathYi.size() == 1) {
                    dentaE[sensor] += bit * TranferEnergy(Tracer.distanceToSink[sensor]);
                } else {
                    dentaE[sensor] += bit * TranferEnergy(getDistance(sensors.get(sensor), sensors.get(pathYi.get(i+1)))) ; //Distance[sensor][pathYi.get(i + 1)]);
                }
            } else  {
                dentaE[sensor] += bit * Er;
                if (i == pathYi.size() - 1) {
                    dentaE[sensor] += bit * TranferEnergy(Tracer.distanceToSink[sensor]);
                } else {
                    dentaE[sensor] += bit * TranferEnergy(getDistance(sensors.get(sensor), sensors.get(pathYi.get(i+1)))) ;//[sensor][pathYi.get(i + 1)]);
                }
            }
        }
    }
    private static double TranferEnergy(double distance) {
        double result = Et;
        if (distance < Do) {
            result += (Efs * distance * distance);
        } else {
            result += (Emp * distance * distance * distance * distance);
        }
        return result;
    }
}