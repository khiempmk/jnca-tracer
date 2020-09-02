/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tracer.utils;

import lombok.extern.slf4j.Slf4j;
import tracer.model.Node;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public final class SensorUtility {
    public static int MaxColum =10001;
    public static int MaxRow =10001;
    public static int numberColum =200;
    public static int marginPanel =20;
    public static int numberRow = 200;
    public static float mRsValue = 20.0f;
    public static float mRtValue = 4.0f;
    public static int mNumberRobotCycle = 0;
   // public static List<Coord> mListNodes = new ArrayList<>();
    public static List<Node> mListSensorNodes = new ArrayList<>();
    public static List<Node> mListTargetNodes = new ArrayList<>();
    public static List<Node> mListSinkNodes = new ArrayList<>();
   // public static List<List<Coord>> mListRobotNodes = new ArrayList<List<Coord>>();
//    public static List<List<Coord>> mListofListSensor = new ArrayList<List<Coord>>();
//    public static List<List<Coord>> mListofListPath = new ArrayList<>();
    public static List<List<Double>> mListofListPathTime = new ArrayList<>();
    public static List<Double> mListofListTime = new ArrayList<>();
    public static List<List<List<Integer>>> mListofListCMLCT = new ArrayList<>();
    public static List<Color> mListColor = new ArrayList<>();
    public static List<Integer> mPathSensor;
    public static int MaxTargetNodes = 30000;
    public static int MaxCycleRobots = 1000;
    public static int MaxRobotNodesCycle = 1000;
    public static float MAX_DISTANCE = 1000000000000.0f;
    
    public static int Lvalue = 2;
    public static int LifeTimeOfSensor = 3000;
    public static float mSaiso = 0.001f;
      
    
    //Constant wuth sink
    public static float mRcValue = 40f;
    public static float mEsValue = 100.0f;
    public static float mEtValue = 50.0f;
    public static float mErValue = 100.0f;
    public static float mEfsValue = 0.01f;
    public static float mEmpValue = 0.0000013f;
    public static float mBitValue = 16.0f;
    public static float mTstamp = 1.0f;
    public static int mMaxHopper = 5;
    public static float mEoValue = 20000000000.0f;
    public static float mOffset = mEoValue/100000;
    public static float mGamma1 = 0.3f;
    public static float mGamma2 = 0.3f;
    public static int mKmax = 7;
    public static float mUnitTime = 60000000;
    
//    public static void writeFile(String filename) throws IOException {
//        File file = new File(filename);
//        if (!file.exists()) file.createNewFile();
//        BufferedWriter outputWriter;
//
//        outputWriter = new BufferedWriter(new FileWriter(file, false));
//        //Write Width and height , Rs, Rt
//        outputWriter.write(numberColum + " "+ numberRow+" "+mRsValue+ " "+mRtValue);
//        outputWriter.newLine();
//        //Write parameter of energy
//        outputWriter.write(mRcValue + " "+ mEsValue+" "+mEtValue+ " "+mErValue+ " "+mEfsValue+" "+mEmpValue+" "+mBitValue+ " "+mMaxHopper+ " "+Lvalue);
//        outputWriter.newLine();
//        //Write number Target, number Robot Cycle, number Sensor
//        outputWriter.write(mListTargetNodes.size() + " "+ mListRobotNodes.size()+" "+mListSensorNodes.size()+ " "+ mListSinkNodes.size());
//        outputWriter.newLine();
//        //Write List point Target
//        for (int i = 0; i < mListTargetNodes.size(); i++) {
//            // Maybe:
//            outputWriter.write(mListTargetNodes.get(i).getX() + " "+ mListTargetNodes.get(i).getY()+" ");
//        }
//        outputWriter.newLine();
//        //Write Robot Cycle
//        for (int i =0;i < mListRobotNodes.size();i++){
//            List<NodeItem> temp = mListRobotNodes.get(i);
//            outputWriter.write(temp.size()+"");
//            outputWriter.newLine();
//            for (int j =0;j < temp.size();j++) {
//               outputWriter.write(temp.get(j).getX()+" "+temp.get(j).getY()+" "+temp.get(j).getGroup()+" ");
//            }
//            outputWriter.newLine();
//
//        }
//        //Write List Sensor
//        for (int i = 0; i < mListSensorNodes.size(); i++) {
//            // Maybe:
//            outputWriter.write(mListSensorNodes.get(i).getX() + " " + mListSensorNodes.get(i).getY() + " ");
//        }
//        outputWriter.newLine();
//
//        //Write List Sink
//        for (int i = 0; i < mListSinkNodes.size(); i++) {
//            // Maybe:
//            outputWriter.write(mListSinkNodes.get(i).getX() + " " + mListSinkNodes.get(i).getY() + " ");
//        }
//        outputWriter.newLine();
//
//
//        outputWriter.flush();
//        outputWriter.close();
//
//    }
//
//    public static void writeTestFile(String filename, List<List<List<Integer>>> ListPathY) throws IOException {
//        File file = new File(filename);
//        if (!file.exists()) file.createNewFile();
//        BufferedWriter outputWriter;
//
//        outputWriter = new BufferedWriter(new FileWriter(file, false));
//
//        int Target = ListPathY.size();
//        //Write Width and height
//        outputWriter.write(Target +" "+ mMaxHopper);
//        outputWriter.newLine();
//
//        for (int i = 0 ; i < Target ; i ++) {
//            List<List<Integer>> pathY = ListPathY.get(i);
//            outputWriter.write(i +" : "+ pathY.size());
//            outputWriter.newLine();
//            for (int j = 0; j < pathY.size(); j++) {
//               List<Integer> Yi = pathY.get(j);
//               for (int k = 0 ; k < Yi.size();k++) {
//                    outputWriter.write(Yi.get(k) + " ");
//               }
//               outputWriter.newLine();
//            }
//
//        }
//
//
//
//
//        outputWriter.flush();
//        outputWriter.close();
//
//
//    }
    public static void readFile(String filename) throws IOException {
        log.info("Reading input {}", filename);
        mListSensorNodes.clear();
//        mListRobotNodes.clear();
        mListTargetNodes.clear();
        mListSinkNodes.clear();
        File file = new File(filename);
        Scanner scanner = new Scanner(file);
        //Read width , height, RsValue, mRtValue
        numberColum = scanner.nextInt();
        numberRow = scanner.nextInt();
        mRsValue = scanner.nextFloat();
        mRtValue = scanner.nextFloat();
        
        //Read parameter of energy
        mRcValue = scanner.nextFloat();
        mEsValue = scanner.nextFloat();
        mEtValue = scanner.nextFloat();
        mErValue = scanner.nextFloat();
        mEfsValue = scanner.nextFloat();
        mEmpValue = scanner.nextFloat();
        mBitValue = scanner.nextFloat();
        mMaxHopper = scanner.nextInt();
        Lvalue = scanner.nextInt();

        ////Read number Target, number Robot Cycle, number Sensor
        int sizeTarget = scanner.nextInt();
        int sizeRobotCycle = scanner.nextInt();
        int sizeSensor = scanner.nextInt();
        int sizeSink = scanner.nextInt();
        // Read Target
        for (int i = 0; i < sizeTarget; i++) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            mListTargetNodes.add(new Node(i,x, y));
        }
        //Read Robot Cycle
        for (int i = 0; i < sizeRobotCycle; i++) {
           // mListRobotNodes.add(new ArrayList<>());
            int sizeRobot = scanner.nextInt();
            for (int j = 0; j < sizeRobot; j++) {
                int x = scanner.nextInt();
                int y = scanner.nextInt();
                int group = scanner.nextInt();
              //  mListRobotNodes.get(i).add(new NodeItem(x, y, 1, group));
            }
        }

        //Read Sensor
        for (int i = 0; i < sizeSensor; i++) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            Node coord=  new Node(i,x, y) ;
            mListSensorNodes.add(coord);
        }
        
        //Read Sink
        for (int i = 0; i < sizeSink; i++) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            mListSinkNodes.add(new Node(i,x, y));
        }

    }
    public static int ccs_size ;
    public static List<List<List<Integer>>> ccs_list = new ArrayList<>();
    public static List<Double> ccs_life_time = new ArrayList<>();
    public static void readCCS(String fileName){
        try {
            log.info("Reading ccs : {}", fileName);
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            String s=  scanner.nextLine();
            s=  scanner.nextLine();
            s= scanner.nextLine();
            s= scanner.nextLine();
            ccs_size = scanner.nextInt() ;

            for (int i =0 ; i < ccs_size ; i++){
                List<List<Integer>> ccp_list = new ArrayList<>();
                int ccp_list_size = scanner.nextInt() ;
                double life_time = scanner.nextDouble();
                for (int j = 0 ; j < ccp_list_size ; j++){
                    List<Integer> ccp = new ArrayList<>();
                    int ccp_size = scanner.nextInt();
                    for (int k = 0; k < ccp_size ; k++) {
                        int sensor = scanner.nextInt();
                        ccp.add(sensor);
                    }
                    ccp_list.add(ccp);
                }
                ccs_list.add(ccp_list);
                ccs_life_time.add(life_time);
            }

        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    public static void captureScreen(Component component,int width, int height,String filename) throws IOException {
        BufferedImage img = getScreenShot(component,width,height);
        // write the captured image as a PNG
        ImageIO.write(img, "png", new File(filename));
    }
    
    public static void captureScreen(Component component,String filename) throws IOException {
        BufferedImage img = getScreenShot(component,component.getWidth(),component.getHeight());
        // write the captured image as a PNG
        ImageIO.write(img, "png", new File(filename));
    }

    public static BufferedImage getScreenShot(Component component ,int width, int height) {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // paints into image's Graphics
        component.paint(image.getGraphics());
        return image;
    }
    
    public static int castInt(float value) {
        int result = (int)value;
        if (result +0.5 < value ){
           return result+1;   
        }
        return result;
    }
    
    public static float calculateDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }
        
    public static void resetSetting() {
        numberColum =200;
        numberRow = 200;
        mRsValue = 2.0f;
        mRtValue = 3.0f;
        mNumberRobotCycle = 0;
    //    mListNodes.clear();
        mListSensorNodes.clear();
    //    mListRobotNodes.clear();
        mListTargetNodes.clear();
    }
    
    public static int giaithua(int N) {
        int result = 1;
        for (int i = 1;i <= N; i++) {
            result = result*i;
        }
        return result;
    }
    
    public static int tohop(int N, int K) {
        if (K == 0 || K == N) {
            return 1;
        } else {
            return (giaithua(N)/(giaithua(N-K)*giaithua(K)));
        }
    }


}
