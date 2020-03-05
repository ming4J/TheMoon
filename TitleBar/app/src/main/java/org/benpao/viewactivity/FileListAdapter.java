package org.benpao.viewactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by shaom on 2016/10/23.
 */

public class FileListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private String[] names;
    private long[] sizes;
    private String[] times;
   private String[] types;
   // private String ip="192.168.31.212";
   // private int port=8000;
    private int layoutResId;
  // private DownloadTask[] tasks;
    private Context context;
    public FileListAdapter(Context context, int layoutResId,
                           String[] names, long[] sizes, String[] times,String [] types,  String ip, int port) {
        // TODO Auto-generated constructor stub
        this.context=context;
        this.inflater=LayoutInflater.from(context);
        this.layoutResId=layoutResId;
        this.names=names;
        this.sizes=sizes;
        this.times=times;
       this.types=types;

       // this.tasks=new DownloadTask[names.length];//得到文件列表后，创建异步任务数组
        //this.ip=ip;
       // this.port=port;

    }
    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return names[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView==null){

            convertView=inflater.inflate(R.layout.activity_filelistinfo,null);
            viewHolder=new ViewHolder();
            convertView.setTag(viewHolder);
           viewHolder.fileimgs=(ImageView) convertView.findViewById(R.id.file_view_img);
            viewHolder.filenames=(TextView) convertView.findViewById(R.id.list_file_name);
            viewHolder.filesizes=(TextView) convertView.findViewById(R.id.list_file_size);
            viewHolder.filetimes=(TextView) convertView.findViewById(R.id.list_file_time);


        }else {
            viewHolder=(ViewHolder) convertView.getTag();
            //
//            if(viewHolder.task!=null){//如果视图vh上有关联的任务，要解除关联
//                viewHolder.task.unbind(viewHolder);//任务与视图解除关联
//            }
//            //滚动到的新的当前位置如果有异步任务，说明之前启动过异步任务，则重新关联
//            if(this.tasks[position]!=null){
//                tasks[position].bind(viewHolder);
//            }
        }
        for (int i=0;i<=position;i++){
            if (types[i].equals("doc")||types[i].equals("txt")||types[i].equals("DOC")||types[i].equals("TXT")||types[i].equals("pdf")||types[i].equals("docx")||types[i].equals("DOCX")||types[i].equals("xlsx")||types[i].equals("pptx"))
                viewHolder.fileimgs.setImageResource(R.drawable.list_docment);

           else if (types[i].equals("jpg")||types[i].equals("png")||types[i].equals("JPG")||types[i].equals("PNG")||types[i].equals("bmp"))
                viewHolder.fileimgs.setImageResource(R.drawable.list_pic);

           else if (types[i].equals("mp3")||types[i].equals("aac")||types[i].equals("wma")||types[i].equals("ape")||types[i].equals("flac")||types[i].equals("APE")||types[i].equals("FLAC"))
                viewHolder.fileimgs.setImageResource(R.drawable.list_music);

           else if (types[i].equals("mp4")||types[i].equals("MP4")||types[i].equals("avi")||types[i].equals("AVI")||types[i].equals("wmv")||types[i].equals("3GP")||types[i].equals("flv"))
                viewHolder.fileimgs.setImageResource(R.drawable.list_video);

           else if (types[i].equals("zip")||types[i].equals("rar")||types[i].equals("7z")||types[i].equals("iso"))
                viewHolder.fileimgs.setImageResource(R.drawable.list_zip);
            else{
                viewHolder.fileimgs.setImageResource(R.drawable.list_other);
            }
            if(sizes[i]<0){

                viewHolder.fileimgs.setImageResource(R.drawable.list_dir);
            }

        }
       // viewHolder.fileimgs.setImageResource(R.drawable.list_other);
        String name=names[position];
        long size=sizes[position];
        String time=times[position];
        //以后拓展filetypeimg
        viewHolder.filenames.setText(name);
        if(size>=0){
            viewHolder.filesizes.setText(FileSizeUtil.FormetFileSize(size));
        }else{
            viewHolder.filesizes.setText("文件夹");
        }

        viewHolder.filetimes.setText(time);
        viewHolder.position=position;
        return convertView;


    }
    class ViewHolder{
        ImageView fileimgs;
        TextView filenames;
        TextView filesizes;
        TextView filetimes;
        int position;
      //  DownloadTask task;
    }
//    class DownloadTask extends AsyncTask<String, Integer, Boolean> {
//
//        ViewHolder vh;//用来关联vh
//        //		int progress=new Random().nextInt(100);//任务的当前执行进度值,先给一个假的进度值
//        int progress;//任务的当前执行进度值
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            String name = params[0];//得到文件名
//            //开始下载文件
//            try {
//                Log.i("download", "连接");
//                Socket s = new Socket(ip, port);
//                Log.i("download", "已经连接");
//                Request req = new Request();
////				req.setWhat(Request.WHAT_DOWNLOAD);
//                req.setWhat(What.DOWNLOAD);//此处使用枚举
//                req.setName(name);
//                Log.i("download", "发送Request");
//                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
//                out.writeObject(req);
//                out.flush();
//                Log.i("download", "Request已经发送");
//                Log.i("download", "接收Response");
//                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
//                Response resp = (Response) in.readObject();
//                Log.i("download", "Response已经接收");
//                name = resp.getName();
//                long size = resp.getSize();
//                long lm=resp.getLastModify();
//                //long lm = resp.getLastModify();
//                Log.i("download", "文件名" + name);
//                Log.i("download", "字节量" + size);
//               Log.i("download", "修改时间" + lm);
//
//
//                Log.i("download", "开始接收文件内容");
//
//                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//                File file = new File(dir, name);
//                RandomAccessFile raf = new RandomAccessFile(file, "rw");
//                raf.setLength(size);//会立即分配空间,如果空间不足，会出异常
//
//                InputStream sin = s.getInputStream();
//                byte[] buf = new byte[8192];
//                int n;
//                long count = 0;//已经下载的字节量
//                while ((n = sin.read(buf)) != -1) {
//                    raf.write(buf, 0, n);
//                    count += n;
//                    int x = (int) (100 * count / size);
//                    //如果文件很大，不必要每次都发布进度
//                    if (x != progress) {
//                        publishProgress(x);
//                        progress = x;
//                    }
//                    if (count >= size) {
//                        break;
//                    }
//
//                }
//                raf.close();
//                //如果文件大小为0，则会出现进度停在0，不会更新到100的情况，所以，增加下面的语句
//                publishProgress(100);
//
//                Log.i("download", "文件接收完成");
//                Log.i("download", "断开");
//                s.close();
//
//                return true;
//            } catch (Exception e) {
//                Log.e("download", "文件下载失败", e);
//                return false;
//            }
//
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            if (!result) {
//                Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//
//        //当前任务与视图解除关联，即双方的引用都置null
//        public void unbind(ViewHolder vh) {
//            this.vh = null;
//            vh.task = null;
//
//            //在视图中显示默认样式，
//            // vh.pb.setProgress(0);//滚动条归0
//            // vh.bt.setEnabled(true);//按钮可点击
//
//        }
//
//        //当前任务与视图进行关联，即互相引用
//        public void bind(ViewHolder vh) {
//            this.vh = vh;
//            vh.task = this;
//
//            //在视图中显示当前进度信息
//            //vh.pb.setMax(100);
//            //vh.pb.setProgress(progress);
//            //vh.bt.setEnabled(false);//按钮变灰，防止重复点击
//        }
//    }
}





