import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jnetpcap.PcapIf;


public class ApplicationLayer extends JFrame implements BaseLayer {
   public int nUpperLayerCount = 0;
   public String pLayerName = null;
   public BaseLayer p_UnderLayer = null;
   public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

   public static LayerManager m_LayerMgr = new LayerManager();

   private JTextField ChattingWrite;
   private JTextField dstIpWrite;
   private JTextField routeDestinationWrite;
   private JTextField routeNetMaskWrite;
   private JTextField routeGatewayWrite;
   private JTextField routeInterfaceWrite;
   private JTextField proxyDeviceWrite;
   private JTextField proxyIpWrite;
   private JTextField proxyMacWrite;
   
   Container contentPane;

   JTextArea routerTableArea;
   JTextArea srcMacAddress;
   JTextArea srcIpAddress;
   JTextArea cacheArea;
   JTextArea proxyArpArea;
   JTextArea fileArea;
   JTextArea ChattingArea;
   

   JLabel lblsrc;
   JLabel lbldst;
   JLabel dstIpLabel;
   JLabel proxyDevice;
   JLabel proxyIp;
   JLabel proxyMac;
   JLabel routeDestination;
   JLabel routeNetMask;
   JLabel routeGateway;
   JLabel routeInterface;
   

   JButton Setting_Button;
   
   JButton Chat_send_Button;
   JButton File_send_Button;
   JButton openFileButton;
   JButton itemDeleteButton;
   JButton allDeleteButton;
   JButton dstIpSendButton;
   JButton proxyAddButton;
   JButton proxyDeleteButton;
   JButton routeAddButton;
   JButton routeRemoveButton;
   
   JCheckBox up;
   JCheckBox gateway;
   JCheckBox host;
   
   static JComboBox<String> NICComboBox;
   static JComboBox<String> NICComboBox2;

   int adapterNumber1 = 0;
   int adapterNumber2 = 0;
   String port;
   byte[] srcIPNumber, dstIPNumber, srcMacNumber;
   String Text;
   JProgressBar progressBar;

   File file;
   
   private ArrayList<ArrayList<byte[]>> cacheTable = new ArrayList<ArrayList<byte[]>>();
   public static RoutingTable routingTable = new RoutingTable();
   
   public String myIp1 = new String();
   public String myMac1 = new String();
   public String myIp2 = new String();
   public String myMac2 = new String();
   
   public static void main(String[] args) {

      ////////////////

      m_LayerMgr.AddLayer(new NILayer("NI"));
      m_LayerMgr.AddLayer(new NILayer("NI2"));
      m_LayerMgr.AddLayer(new EthernetLayer("Ethernet"));
      m_LayerMgr.AddLayer(new EthernetLayer("Ethernet2"));
      m_LayerMgr.AddLayer(new ARPLayer("ARP"));
      m_LayerMgr.AddLayer(new ARPLayer("ARP2"));
      m_LayerMgr.AddLayer(new IPLayer("IP"));
      m_LayerMgr.AddLayer(new IPLayer("IP2"));
      m_LayerMgr.AddLayer(new ApplicationLayer("GUI"));
      // m_LayerMgr.AddLayer(new ChatAppLayer("ChatApp"));
      // m_LayerMgr.AddLayer(new FileAppLayer("FileApp"));
      
//      m_LayerMgr.ConnectLayers(" NI ( *Ethernet ( *ARP ( *IP ( *GUI ) ) +IP ( *GUI ) ) )  ^NI2 ( *Ethernet2 ( *ARP2 ( *IP2 ( *GUI ) ) +IP2 ( *GUI ) ) )");
      m_LayerMgr.ConnectLayers(" NI ( *Ethernet ( *ARP ( *IP ( *GUI ) ) +IP ( *GUI ) ) )");
      m_LayerMgr.ConnectLayers(" NI2 ( *Ethernet2 ( *ARP2 ( *IP2 ( *GUI ) ) +IP2 ( *GUI ) ) )");
      ((IPLayer) m_LayerMgr.GetLayer("IP")).setAnotherPortSet (((IPLayer) m_LayerMgr.GetLayer("IP2")));
      ((IPLayer) m_LayerMgr.GetLayer("IP2")).setAnotherPortSet (((IPLayer) m_LayerMgr.GetLayer("IP")));
      ((IPLayer) m_LayerMgr.GetLayer("IP")).setRouter(routingTable);
      ((IPLayer) m_LayerMgr.GetLayer("IP2")).setRouter(routingTable);
   }

   class setAddressListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {

         if (e.getSource() == Setting_Button) {
        	/* Reset 눌렀을 때 */
            if (Setting_Button.getText() == "Reset") {
               srcMacAddress.setText("");
               srcIpAddress.setText("");
               Setting_Button.setText("Setting");
               srcMacAddress.setEnabled(true);
               srcIpAddress.setEnabled(true);
            } 
            /* 자신의 MAC과  자신의IP 주소 세팅 */
            else {
            	byte[] myIpByte = new byte[4];   //ip setting
                String[] splitIp = myIp1.split("\\.");
                for (int i = 0; i < 4; i++) {
                	myIpByte[i] = (byte) Integer.parseInt(splitIp[i], 10);
                }
                
                /* -을  제거하여 저장 */
                byte[] myMacByte = new byte[6];    //mac setting
                String[] splitMac = myMac1.split("-");
                for (int i = 0; i < 6; i++) {
                	myMacByte[i] = (byte) Integer.parseInt(splitMac[i], 16);
                }
                
                /* .을 제거하여 저장*/
                byte[] myIpByte2 = new byte[4];			//ip2 setting
                String[] splitIp2 = myIp2.split("\\.");
                for (int i = 0; i < 4; i++) {
                	myIpByte2[i] = (byte) Integer.parseInt(splitIp2[i], 10);
                }
                
                byte[] myMacByte2 = new byte[6];		//mac2 setting
                String[] splitMac2 = myMac2.split("-");
                for (int i = 0; i < 6; i++) {
                	myMacByte2[i] = (byte) Integer.parseInt(splitMac2[i], 16);
                }
                
//                System.out.println("My ip1:"+myIp1);
//                System.out.println("My ip2:"+myIp2);
//                System.out.println("My Mac1:"+myMac1);
//                System.out.println("My Mac2:"+myMac2);
               
               System.out.println("Setting");
                
               /* ARP 프레임에 입력한 송신지 MAC, IP 주소와 수신지 MAC 주소 저장 */
               ((ARPLayer) m_LayerMgr.GetLayer("ARP")).SetIpSrcAddress(myIpByte);
               ((ARPLayer) m_LayerMgr.GetLayer("ARP2")).SetIpSrcAddress(myIpByte2);
               ((ARPLayer) m_LayerMgr.GetLayer("ARP")).SetArpSrcAddress(myMacByte);
               ((ARPLayer) m_LayerMgr.GetLayer("ARP2")).SetArpSrcAddress(myMacByte2);
               
               /* IPLayer 헤더에  port num, 송수신 ip 주소 저장 */ 
               ((IPLayer) m_LayerMgr.GetLayer("IP")).SetIpSrcAddress(myIpByte);
               ((IPLayer) m_LayerMgr.GetLayer("IP2")).SetIpSrcAddress(myIpByte2);
               ((IPLayer) m_LayerMgr.GetLayer("IP")).setPort(1);
               ((IPLayer) m_LayerMgr.GetLayer("IP2")).setPort(2);
              
               /* 이더넷 헤더에 송수신지 MAC 주소 저장 */
               ((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).SetEnetSrcAddress(myMacByte);
               ((EthernetLayer) m_LayerMgr.GetLayer("Ethernet2")).SetEnetSrcAddress(myMacByte2);
               ((NILayer) m_LayerMgr.GetLayer("NI")).SetAdapterNumber(adapterNumber1);
               ((NILayer) m_LayerMgr.GetLayer("NI2")).SetAdapterNumber(adapterNumber2);
            }
         }
         
         if (e.getSource() == openFileButton) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(filter);
            int ret = chooser.showOpenDialog(null);
            
            if (ret == JFileChooser.APPROVE_OPTION) {
               String filePath = chooser.getSelectedFile().getPath();
               fileArea.setText(filePath);
               File_send_Button.setEnabled(true);
               file = chooser.getSelectedFile();
            }
         }
         if(e.getSource() == allDeleteButton){
        	 cacheArea.setText(null);
         }
         
         /* 
          * Proxy ARP Table
          * Add 버튼 눌렀을 때
          */
         if (e.getSource() == proxyAddButton) {
            if (proxyAddButton.getText() == "Add") {
               String proxyDevice = proxyDeviceWrite.getText();  // 프록시 디바이스 이름
               String proxyIP = proxyIpWrite.getText();          // 추가할 IP
               String proxyMac = proxyMacWrite.getText();        // 추가할 MAC
               
               /* 한 줄에 추가 */
               proxyArpArea.append(proxyDevice);
               proxyArpArea.append("  " + proxyIP);
               proxyArpArea.append("  " + proxyMac + "\n");
               
               byte[] proxyDeviceByte = new byte[1];
               byte[] proxyIpByte = new byte[4];
               byte[] proxyMacByte = new byte[6];
               
               /* .을 제거하여 저장 */
               String[] split_proxyIP = proxyIP.split("\\.");
               for (int i = 0; i < 4; i++) {
                  proxyIpByte[i] = (byte) Integer.parseInt(split_proxyIP[i], 10);
               }
               
               /* -을 제거하여 저장 */
               String[] split_proxyMac = proxyMac.split("-");
               for (int i = 0; i < 6; i++) {
                  proxyMacByte[i] = (byte) Integer.parseInt(split_proxyMac[i], 16);
               }
               
               proxyDeviceByte[0] = (byte)Integer.parseInt("1");
               ((ARPLayer)m_LayerMgr.GetLayer("ARP")).addProxyTable(proxyDeviceByte, proxyIpByte, proxyMacByte);
            }   
         }
         
         /* File send 버튼 눌렀을 때 */ 
         if (e.getSource() == File_send_Button) {
            ((FileAppLayer) m_LayerMgr.GetLayer("FileApp")).setAndStartSendFile();
            File_send_Button.setEnabled(false);
         }
         
         /* send 버튼 눌렀을 때 */
         if (e.getSource() == Chat_send_Button) {
//             if (Setting_Button.getText() == "Reset") { 
//                String input = ChattingWrite.getText();           // 채팅창에 입력된 텍스트 저장
//                ChattingArea.append("[SEND] : " + input + "\n");  // 성공하면 입력값 출력
//                byte[] bytes = input.getBytes(); 				  // 입력된 메시지를 바이트로 저장
//                
//                // 채팅창에 입력된 메시지를 chatAppLayer로 보냄
//                ((ChatAppLayer)m_LayerMgr.GetLayer("ChatApp")).Send(bytes, bytes.length);
//                
//                ChattingWrite.setText("");  // 채팅 입력란 비우기
//             } 
//             else {
//                JOptionPane.showMessageDialog(null, "Address Setting Error!.");  // 주소 설정 에러
//                return;
//             }
          }
          /* 파일 선택 버튼 */
          else if (e.getSource() == openFileButton) {
             JFileChooser fileChooser = new JFileChooser();      // 파일 선택 객체 생성
             fileChooser.setCurrentDirectory(new File("C:\\"));  // 파일 창의 기본 경로를 C:\\에서 시작
             int result = fileChooser.showOpenDialog(null);      // 창 띄우기
             
             /* 선택 없이 취소 눌렀을 경우 */
             if(result != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null, "파일 선택 오류");
                return;
             }
             /* 파일을 선택했을 경우 */
             else {
//                object = fileChooser.getSelectedFile();  // 대상 파일을 선택한 파일로 변경
//                path.setText(fileChooser.getSelectedFile().getPath().toString());  // 경로 텍스트 필드
                File_send_Button.setEnabled(true);       // 파일 전송 버튼 클릭 가능하도록 변경
             }
          }
          /* 파일 전송 버튼 눌렀을 때 */
          else if (e.getSource() == File_send_Button) {
//             if (Setting_Button.getText() == "Reset") {
//                try {
//                   File_send_Button.setEnabled(false);  // 전송 버튼 클릭 후에는 전송 버튼 비활성화
//                   
//                   /* 
//                    * 파일창에 입력된 메시지를 FileAppLayer로 보냄
//                    * 이 때, 실시간 progressbar 변경 위해 생성한 메소드인 sendFile() 호출
//                    */
//                   ((FileAppLayer)m_LayerMgr.GetLayer("FileApp")).sendFile();
//                } catch (Exception e1) {
//                   // TODO Auto-generated catch block
//                   e1.printStackTrace();
//                }
//             }
//             else {
//                JOptionPane.showMessageDialog(null, "Address Setting Error!.");  // 주소 설정 에러
//                return;
//             }
          }
      
         // basic ARP 전송
         if (e.getSource() == dstIpSendButton) {
            if (dstIpSendButton.getText() == "Send") {
               String dstIP = dstIpWrite.getText();
               /* 입력한IP 주소*/
               cacheArea.append(dstIP);
               cacheArea.append("  ??-??-??-??-??-??");
               cacheArea.append("  Incomplete" + "\n");
               
               /* 입력한 IP 주소 상태 테이블에 표시*/
               byte[] dstIPAddress = new byte[4];
               String[] byte_dstIP = dstIP.split("\\.");
               for (int i = 0; i < 4; i++) {
                  dstIPAddress[i] = (byte) Integer.parseInt(byte_dstIP[i], 10);
               }
               
               dstIPNumber = dstIPAddress;
              // ((TCPLayer) m_LayerMgr.GetLayer("TCP")).ARPSend(srcIPNumber, dstIPNumber); 
            }
         }
         
         /* route 테이블 delete 버튼 */
        if (e.getSource() == routeRemoveButton) {
            System.out.println(">> routing table show 삭제");
            routerTableArea.setText(null);
            
            ((IPLayer) m_LayerMgr.GetLayer("IP")).removeRoutingTable();
          }
        /* route테이블에 route 추가하는  add button */
        if (e.getSource() == routeAddButton) {
        	String routeDestination = routeDestinationWrite.getText();
        	String routeNetmask = routeNetMaskWrite.getText();
        	String routeGateway = routeGatewayWrite.getText();
        	String upIsChecked = "0";
        	String gatewayIsChecked = "0";
        	String hostIsChecked = "0";
        	String tag="";
        	
        	if (up.isSelected()) {
        		upIsChecked = "1";
        		tag+="U";
        	}
        	if (gateway.isSelected()) {
        		gatewayIsChecked = "1";
        		tag+="G";
        	}
        	if (host.isSelected()) {
        		hostIsChecked = "1";
        		tag+="H";
        	}
        	
        	port = routeInterfaceWrite.getText();
        	
        	routerTableArea.append(routeDestination+"      ");
        	routerTableArea.append(routeNetmask+"      ");
        	routerTableArea.append(routeGateway+"      ");
        	routerTableArea.append(tag+"      ");
        	routerTableArea.append(port + "\n");
        	
        	byte[] dstIPAddress = new byte[4];
            String[] byte_dstIP = routeDestination.split("\\.");
            for (int i = 0; i < 4; i++) {
               dstIPAddress[i] = (byte) Integer.parseInt(byte_dstIP[i], 10);
            }
            byte[] netMaskAddress = new byte[4];
            String[] byte_mask = routeNetmask.split("\\.");
            for (int i = 0; i < 4; i++) {
            	netMaskAddress[i] = (byte) Integer.parseInt(byte_mask[i], 10);
            }
            byte[] gatewayAddress = new byte[4];
            if (routeGateway.equals("*")){
            	gatewayAddress[0] = (byte) Integer.parseInt("-1", 10);
            }
            else {
            	String[] byte_gate = routeNetmask.split("\\.");
            	 for (int i = 0; i < 4; i++) {
                 	netMaskAddress[i] = (byte) Integer.parseInt(byte_gate[i], 10);
                 }
            }
            
            byte[] flagAddress = new byte[3];
            flagAddress[0] = (byte) Integer.parseInt(upIsChecked,10);
            flagAddress[1] = (byte) Integer.parseInt(gatewayIsChecked,10);
            flagAddress[2] = (byte) Integer.parseInt(hostIsChecked,10);
            
            byte[] interfaceAddress = new byte[1];
            interfaceAddress[0] =  (byte) Integer.parseInt(port,10);
            ((IPLayer) m_LayerMgr.GetLayer("IP")).addRoutingTable(dstIPAddress, netMaskAddress, gatewayAddress, flagAddress, interfaceAddress);
        
        }
       
      }

   }

   public ApplicationLayer(String pName) {
      pLayerName = pName;

      setTitle("Packet_Send_Test");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(250, 0, 1200, 800);
      contentPane = new JPanel();
      ((JComponent) contentPane).setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);

      // ARP Cache panel
      JPanel arpCachePanel = new JPanel();
      arpCachePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "ARP Cache",
            TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
      arpCachePanel.setBounds(10, 5, 370, 371);
      contentPane.add(arpCachePanel);
      arpCachePanel.setLayout(null);

      JPanel arpCacheEditorPanel = new JPanel();
      arpCacheEditorPanel.setBounds(10, 15, 350, 230);
      arpCachePanel.add(arpCacheEditorPanel);
      arpCacheEditorPanel.setLayout(null);

      cacheArea = new JTextArea();
      cacheArea.setEditable(false);
      cacheArea.setBounds(0, 0, 350, 220);
      arpCacheEditorPanel.add(cacheArea);// chatting panel 

      itemDeleteButton = new JButton("Delete Item");
      itemDeleteButton.setBounds(70, 250, 100, 30);

      allDeleteButton = new JButton("Delete All");
      allDeleteButton.setBounds(200, 250, 100, 30);
      allDeleteButton.addActionListener(new setAddressListener());
      
      /* Delete button actionListener */
      arpCachePanel.add(itemDeleteButton);
      arpCachePanel.add(allDeleteButton);
      

      dstIpLabel = new JLabel("IP 주소");
      dstIpLabel.setBounds(15, 300, 100, 20);
      arpCachePanel.add(dstIpLabel);

      dstIpWrite = new JTextField();
      dstIpWrite.setBounds(70, 300, 200, 20);// 249
      arpCachePanel.add(dstIpWrite);
      dstIpWrite.setColumns(10);// target IP write panel 
      dstIpSendButton = new JButton("Send");
      dstIpSendButton.addActionListener(new setAddressListener());
      dstIpSendButton.setBounds(285, 300, 70, 20);
      arpCachePanel.add(dstIpSendButton);

      
     
      // routing table show panel
      JPanel routePannel = new JPanel();
      routePannel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Routing Table",
            TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
      routePannel.setBounds(10, 380, 360, 260);
      contentPane.add(routePannel);
      routePannel.setLayout(null);

      JPanel routerTableEditorPanel = new JPanel();// write panel
      routerTableEditorPanel.setBounds(10, 15, 340, 235);
      routePannel.add(routerTableEditorPanel);
      routerTableEditorPanel.setLayout(null);

      routerTableArea = new JTextArea();
      routerTableArea.setEditable(false);
      routerTableArea.setBounds(0, 0, 340, 190);
      routerTableEditorPanel.add(routerTableArea);// routing show edit

      routerTableArea.setLayout(null);
      
      	//router show table에서의 remove button
      routeRemoveButton = new JButton("Remove");
      routeRemoveButton.setBounds(120, 205, 100, 30);
      
      routeRemoveButton.addActionListener(new setAddressListener());
      routerTableEditorPanel.add(routeRemoveButton);

      // router add panel
      JPanel routerAddPanel = new JPanel();// router add panel
      routerAddPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Add Routing Table",
            TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
      routerAddPanel.setBounds(380, 380, 350, 250);
      
      routerAddPanel.setLayout(null);

      routeDestination = new JLabel("Destination");
      routeDestination.setBounds(20, 40, 80, 20);
      routerAddPanel.add(routeDestination);

      routeNetMask = new JLabel("Netmask");
      routeNetMask.setBounds(20, 70, 80, 20);
      routerAddPanel.add(routeNetMask);
      
      routeGateway = new JLabel("Gateway");
      routeGateway.setBounds(20, 100, 80, 20);
      routerAddPanel.add(routeGateway);
      
      routeGateway = new JLabel("Flag");
      routeGateway.setBounds(20, 130, 80, 20);
      routerAddPanel.add(routeGateway);
      
      routeInterface = new JLabel("Interface");
      routeInterface.setBounds(20, 160, 80, 20);
      routerAddPanel.add(routeInterface);
      
      routeDestinationWrite = new JTextField();
      routeDestinationWrite.setBounds(100, 40, 200, 20);
      routerAddPanel.add(routeDestinationWrite);

      routeNetMaskWrite = new JTextField();
      routeNetMaskWrite.setBounds(100, 70, 200, 20);
      routerAddPanel.add(routeNetMaskWrite);

      routeGatewayWrite = new JTextField();
      routeGatewayWrite.setBounds(100, 100, 200, 20);
      routerAddPanel.add(routeGatewayWrite);
      
      routeInterfaceWrite = new JTextField();
      routeInterfaceWrite.setBounds(100, 160, 200, 20);
      routerAddPanel.add(routeInterfaceWrite);
      
      routeAddButton = new JButton("Add");
      routeAddButton.setBounds(130, 200, 70, 30);
      
      routeAddButton.addActionListener(new setAddressListener());
      
      up = new JCheckBox("up",true);
      up.setBounds(100, 130, 50, 20);
	  gateway = new JCheckBox("gateway");
	  gateway.setBounds(150, 130, 80, 20);
	  host = new JCheckBox("host",true);
	  host.setBounds(230, 130, 100, 20);
		
	  routerAddPanel.add(up);
	  routerAddPanel.add(gateway);
	  routerAddPanel.add(host);
	  routerAddPanel.add(routeAddButton);
	  
	  contentPane.add(routerAddPanel);
      setVisible(true);
      
      /* Setting panel */
      JPanel settingPanel = new JPanel();
      settingPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Setting",
            TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
      settingPanel.setBounds(380, 5, 350, 370);
      contentPane.add(settingPanel);
      settingPanel.setLayout(null);

      JLabel NICLabel = new JLabel("NIC1 Select");
      NICLabel.setBounds(30, 20, 300, 20);
      settingPanel.add(NICLabel);
      
      JLabel NICLabel2 = new JLabel("NIC2 Select");
      NICLabel2.setBounds(30, 100, 300, 20);
      settingPanel.add(NICLabel2);
      
      NICComboBox = new JComboBox();
      NICComboBox.setBounds(30, 49, 300, 20);
      settingPanel.add(NICComboBox);
      
      NICComboBox2 = new JComboBox();
      NICComboBox2.setBounds(30, 129, 300, 20);
      settingPanel.add(NICComboBox2);
      
      Setting_Button = new JButton("Setting");// setting
      Setting_Button.setBounds(80, 180, 200, 40);
      Setting_Button.addActionListener(new setAddressListener());
      JPanel settingBtnPannel = new JPanel();
      settingBtnPannel.setBounds(290, 129, 150, 20);
      settingPanel.add(Setting_Button);// setting
      
      contentPane.add(settingBtnPannel);
      
      for (int i = 0; ((NILayer) m_LayerMgr.GetLayer("NI")).m_pAdapterList.size() > i; i++) {
         NICComboBox.addItem(((NILayer) m_LayerMgr.GetLayer("NI")).m_pAdapterList.get(i).getDescription());
      }
      for (int i = 0; ((NILayer) m_LayerMgr.GetLayer("NI2")).m_pAdapterList.size() > i; i++) {
          NICComboBox2.addItem(((NILayer) m_LayerMgr.GetLayer("NI2")).m_pAdapterList.get(i).getDescription());
       }
      
      NICComboBox.addActionListener(new ActionListener() { // Event Listener

          @Override
          public void actionPerformed(ActionEvent e) {
             // TODO Auto-generated method stub

             adapterNumber1 = NICComboBox.getSelectedIndex();
//             System.out.println("Index: " + adapterNumber1);
             try {
            	 myMac1 = get_MacAddress(((NILayer) m_LayerMgr.GetLayer("NI")).m_pAdapterList.get(0).getHardwareAddress());
//            	 System.out.println(myMac1);
                 byte[] ipSrcAddress1 = ((((NILayer) m_LayerMgr.GetLayer("NI")).m_pAdapterList.get(0)
              		   .getAddresses()).get(0)).getAddr().getData();
                 final StringBuilder IPAddrbuf0 = new StringBuilder();
                 for (byte b: ipSrcAddress1) {
              	   if (IPAddrbuf0.length()!=0)
              		   IPAddrbuf0.append(".");
              	   IPAddrbuf0.append(b & 0xff);
                 }
                 myIp1 = IPAddrbuf0.toString();
//                 System.out.println(myIp1);
             } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
             }
          }
       });
      
      NICComboBox2.addActionListener(new ActionListener() { // Event Listener

          @Override
          public void actionPerformed(ActionEvent e) {
             // TODO Auto-generated method stub
             adapterNumber2 = NICComboBox2.getSelectedIndex();
//             System.out.println("Index: " + adapterNumber2);
             try {
             	myMac2= get_MacAddress(((NILayer) m_LayerMgr.GetLayer("NI2")).m_pAdapterList.get(1).getHardwareAddress());
//             	System.out.println(myMac2);
             	byte[] ipSrcAddress2 = ((((NILayer) m_LayerMgr.GetLayer("NI2")).m_pAdapterList.get(1)
              		   .getAddresses()).get(0)).getAddr().getData();
                 final StringBuilder IPAddrbuf1 = new StringBuilder();
                 for (byte b: ipSrcAddress2) {
              	   if (IPAddrbuf1.length()!=0)
              		 IPAddrbuf1.append(".");
              	 IPAddrbuf1.append(b & 0xff);
               }
               myIp2 = IPAddrbuf1.toString();
//               System.out.println(myIp2);
             } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
             }
          }
       });
      
      /* proxy panel */
      JPanel proxyArpPanel = new JPanel();
      proxyArpPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Proxy ARP Entry",
            TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
      proxyArpPanel.setBounds(750, 370, 350, 340);  
      contentPane.add(proxyArpPanel);
      proxyArpPanel.setLayout(null);

      JPanel proxyEditorPanel = new JPanel();
      proxyEditorPanel.setBounds(5, 15, 330, 150);
      proxyArpPanel.add(proxyEditorPanel);
      proxyEditorPanel.setLayout(null);

      proxyArpArea = new JTextArea();
      proxyArpArea.setEditable(false);
      proxyArpArea.setBounds(5, 5, 420, 150);
      proxyEditorPanel.add(proxyArpArea);

      JPanel proxyInputPanel = new JPanel();
    
      proxyInputPanel.setBounds(10, 180, 320, 150);
      proxyInputPanel.setLayout(null);
      proxyArpPanel.add(proxyInputPanel);
      proxyDevice = new JLabel("Device");
      proxyDevice.setBounds(20, 10, 60, 20);
      proxyInputPanel.add(proxyDevice);

      proxyIp = new JLabel("IP 주소");
      proxyIp.setBounds(20, 40, 60, 20);
      proxyInputPanel.add(proxyIp);

      proxyMac = new JLabel("MAC 주소");
      proxyMac.setBounds(20, 70, 60, 20);
      proxyInputPanel.add(proxyMac);

      proxyDeviceWrite = new JTextField();
      proxyDeviceWrite.setBounds(100, 10, 200, 20);
      proxyInputPanel.add(proxyDeviceWrite);

      proxyIpWrite = new JTextField();
      proxyIpWrite.setBounds(100, 40, 200, 20);
      proxyInputPanel.add(proxyIpWrite);

      proxyMacWrite = new JTextField();
      proxyMacWrite.setBounds(100, 70, 200, 20);
      proxyInputPanel.add(proxyMacWrite);

      proxyAddButton = new JButton("Add"); 
      proxyAddButton.setBounds(40, 110, 100, 25);  
      proxyDeleteButton = new JButton("Delete");
      proxyDeleteButton.setBounds(180, 110, 100, 25);
      proxyInputPanel.add(proxyAddButton);
      proxyInputPanel.add(proxyDeleteButton);
      
      proxyAddButton.addActionListener(new setAddressListener());
      proxyDeleteButton.addActionListener(new setAddressListener());
      
      /* chatting panel*/
      JPanel chattingPanel = new JPanel();
      chattingPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "chatting",
            TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
      chattingPanel.setBounds(750, 110, 370, 250);
      contentPane.add(chattingPanel);
      chattingPanel.setLayout(null);

      JPanel chattingEditorPanel = new JPanel();
      chattingEditorPanel.setBounds(10, 15, 340, 160);
      chattingPanel.add(chattingEditorPanel);
      chattingEditorPanel.setLayout(null);

      ChattingArea = new JTextArea();
      ChattingArea.setEditable(false);
      ChattingArea.setBounds(0, 0, 340, 210);
      chattingEditorPanel.add(ChattingArea);

      JPanel chattingInputPanel = new JPanel();
      chattingInputPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
      chattingInputPanel.setBounds(10, 190, 230, 25);
      chattingPanel.add(chattingInputPanel);
      chattingInputPanel.setLayout(null);

      ChattingWrite = new JTextField();
      ChattingWrite.setBounds(2, 2, 230, 20);
      chattingInputPanel.add(ChattingWrite);
      ChattingWrite.setColumns(10);

      
      /* file panel */
      JPanel fileTransferPanel = new JPanel();
      fileTransferPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "file transfer",
            TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
      fileTransferPanel.setBounds(750, 10, 370, 90);
      contentPane.add(fileTransferPanel);
      fileTransferPanel.setLayout(null);

      JPanel fileEditorPanel = new JPanel();
      fileEditorPanel.setBounds(10, 20, 330, 60);
      fileTransferPanel.add(fileEditorPanel);
      fileEditorPanel.setLayout(null);

      fileArea = new JTextArea();
      fileArea.setEditable(false);
      fileArea.setBounds(0, 5, 250, 20);
      fileEditorPanel.add(fileArea);

      openFileButton = new JButton("File...");
      openFileButton.setBounds(260, 5, 70, 20);
      openFileButton.addActionListener(new setAddressListener());
      fileEditorPanel.add(openFileButton);

      this.progressBar = new JProgressBar(0, 100);
      this.progressBar.setBounds(0, 40, 250, 20);
      this.progressBar.setStringPainted(true);
      fileEditorPanel.add(this.progressBar);

      File_send_Button = new JButton("Send");
      File_send_Button.setBounds(260, 40, 70, 20);
      fileEditorPanel.add(File_send_Button);
      File_send_Button.addActionListener(new setAddressListener());
      File_send_Button.setEnabled(false);

      Chat_send_Button = new JButton("Send");     
      Chat_send_Button.setBounds(270, 190, 80, 25);
      Chat_send_Button.addActionListener(new setAddressListener());
      chattingPanel.add(Chat_send_Button);

      setVisible(true);
   }

   public File getFile() {
      return this.file;
   }

   public String get_MacAddress(byte[] byte_MacAddress) {

      String MacAddress = "";
      for (int i = 0; i < 6; i++) {
         MacAddress += String.format("%02X%s", byte_MacAddress[i], (i < MacAddress.length() - 1) ? "" : "");
         if (i != 5) {
            MacAddress += "-";
         }
      }

      //System.out.println("present MAC address: " + MacAddress);
      return MacAddress;
   }

   public boolean Receive(byte[] input) {
      if (input != null) {
         byte[] data = input;
         Text = new String(data);
         routerTableArea.append("[RECV] : " + Text + "\n");
         return false;
      }
      return false;
   }

   @Override
   public void SetUnderLayer(BaseLayer pUnderLayer) {
      // TODO Auto-generated method stub
      if (pUnderLayer == null)
         return;
      this.p_UnderLayer = pUnderLayer;
   }

   @Override
   public void SetUpperLayer(BaseLayer pUpperLayer) {
      // TODO Auto-generated method stub
      if (pUpperLayer == null)
         return;
      this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
      // nUpperLayerCount++;
   }

   @Override
   public String GetLayerName() {
      // TODO Auto-generated method stub
      return pLayerName;
   }

   @Override
   public BaseLayer GetUnderLayer() {
      // TODO Auto-generated method stub
      if (p_UnderLayer == null)
         return null;
      return p_UnderLayer;
   }

   @Override
   public BaseLayer GetUpperLayer(int nindex) {
      // TODO Auto-generated method stub
      if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
         return null;
      return p_aUpperLayer.get(nindex);
   }

   @Override
   public void SetUpperUnderLayer(BaseLayer pUULayer) {
      this.SetUpperLayer(pUULayer);
      pUULayer.SetUnderLayer(this);

   }

   // Cache table 세팅 
   public void setArpCache(ArrayList<ArrayList<byte[]>> cacheTable) {
      this.cacheTable = cacheTable;
      cacheArea.setText("");
   
      System.out.println(">> ARP Cache table setting");

      for(int i=0; i<cacheTable.size(); i++) {
         byte[] ip_byte = cacheTable.get(i).get(0);
         byte[] mac_byte = cacheTable.get(i).get(1);
         byte[] status_byte = cacheTable.get(i).get(2);
         
         String ip_Byte1 = Integer.toString(Byte.toUnsignedInt(ip_byte[0]));
         String ip_Byte2 = Integer.toString(Byte.toUnsignedInt(ip_byte[1]));
         String ip_Byte3 = Integer.toString(Byte.toUnsignedInt(ip_byte[2]));
         String ip_Byte4 = Integer.toString(Byte.toUnsignedInt(ip_byte[3]));
         
         String mac_Byte1 = String.format("%02X", mac_byte[0]);
         String mac_Byte2 = String.format("%02X", mac_byte[1]);
         String mac_Byte3 = String.format("%02X", mac_byte[2]);
         String mac_Byte4 = String.format("%02X", mac_byte[3]);
         String mac_Byte5 = String.format("%02X", mac_byte[4]);
         String mac_Byte6 = String.format("%02X", mac_byte[5]);
         
         cacheArea.append(ip_Byte1+"."+ip_Byte2+"."+ip_Byte3+"."+ip_Byte4);
         cacheArea.append("  "+mac_Byte1+"-"+mac_Byte2+"-"+mac_Byte3+"-"+mac_Byte4+"-"+mac_Byte5+"-"+mac_Byte6);
         System.out.println(ip_Byte1+"."+ip_Byte2+"."+ip_Byte3+"."+ip_Byte4);
         System.out.println("  "+mac_Byte1+"-"+mac_Byte2+"-"+mac_Byte3+"-"+mac_Byte4+"-"+mac_Byte5+"-"+mac_Byte6);

         if (byte2ToInt(status_byte[0], status_byte[1])==1) {
            cacheArea.append("  complete" + "\n");
         }
         else {
            cacheArea.append("  Incomplete" + "\n");
         }
         
      }
      
   }
   
   private int byte2ToInt(byte value1, byte value2) {
        return (int)((value1 << 8) | (value2));
    }
}