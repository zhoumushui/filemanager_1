package com.zms.filemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main extends Activity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private ListView fileView;
    private String path = "/sdcard";// 文件路径
    private List<Map<String, Object>> items;
    private SimpleAdapter adapter;
    private File backFile = null;
    private String currentPath = "/sdcard";
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle("文件管理");
        fileView = (ListView) findViewById(R.id.filelist);
        listDir(path);
    }

    /**
     * 动态绑定文件信息到listview上
     *
     * @param path
     */
    private void listDir(String path) {
        items = bindList(path);

        // if(items!=null){
        adapter = new SimpleAdapter(this, items, R.layout.file_row,
                new String[]{"name", "path", "img"}, new int[]{R.id.name,
                R.id.desc, R.id.img});
        fileView.setAdapter(adapter);
        fileView.setOnItemClickListener(this);
        fileView.setOnItemLongClickListener(this);
        fileView.setSelection(0);
        // }
    }

    /**
     * 返回所有文件目录信息
     *
     * @param path
     * @return
     */
    private List<Map<String, Object>> bindList(String path) {
        File[] files = new File(path).listFiles();
        // if(files!=null){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(
                files.length);
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("name", "/sdcard");
        root.put("img", R.drawable.folder);
        root.put("path", "根目录");
        list.add(root);
        Map<String, Object> pmap = new HashMap<String, Object>();
        pmap.put("name", "返回");
        pmap.put("img", R.drawable.back);
        pmap.put("path", "上级目录");
        list.add(pmap);
        for (File file : files) {
            Map<String, Object> map = new HashMap<String, Object>();
            if (file.isDirectory()) {
                map.put("img", R.drawable.folder);
            } else {
                map.put("img", R.drawable.doc);
            }
            map.put("name", file.getName());
            map.put("path", file.getPath());
            list.add(map);
        }
        return list;
        /*
         * } return null;
		 */
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {

        if (position == 0) {// 返回到/sdcard目录
            path = "/sdcard";
            listDir(path);
        } else if (position == 1) {// 返回上一级目录
            toParent();
        } else {
            if (items != null) {
                path = (String) items.get(position).get("path");
                File file = new File(path);
                if (file.canRead() && file.canExecute() && file.isDirectory()) {
                    listDir(path);
                } else {
                    openFile(file);
                    Toast.makeText(this, "呵呵", Toast.LENGTH_SHORT).show();
                }
            }
        }
        backFile = new File(path);
    }

    private void toParent() {// 回到父目录
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent == null) {
            listDir(path);
        } else {
            path = parent.getAbsolutePath();
            listDir(path);
        }
    }

    /**
     * 文件操作提示
     *
     * @param id
     */
    private void myNewDialog(int id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
            case 0:
                LayoutInflater factory = LayoutInflater.from(this);
                final View textEntryView = factory.inflate(R.layout.filedialog,
                        null);
                builder.setTitle("创建文件夹");
                builder.setView(textEntryView);
                builder.setPositiveButton("确定", new CreateDialog(textEntryView));
                builder.setNegativeButton("取消", null);
                break;
            case 1:
                LayoutInflater factory2 = LayoutInflater.from(this);
                final View textEntryView2 = factory2.inflate(R.layout.filedialog,
                        null);
                builder.setTitle("重命名文件");
                builder.setView(textEntryView2);
                builder.setPositiveButton("确定", new RenameDialog(textEntryView2));
                // <span
                // style="font-family:Arial,Helvetica,sans-serif;">builder.setNegativeButton("取消",
                // null);</span>
                builder.setNegativeButton("取消", null);
                break;
            case 2:
                builder.setTitle("确定要删除吗？");
                builder.setPositiveButton("确定", new DeleteDialog());
                builder.setNegativeButton("取消", null);
                break;
        }
        builder.create().show();
    }

    private void mySortDialog(int id) { // 排序

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
            case 0:// 按名称排序
                LayoutInflater factory = LayoutInflater.from(this);
                final View textEntryView = factory.inflate(R.layout.filedialog,
                        null);
                builder.setTitle("创建文件夹");
                builder.setView(textEntryView);
                builder.setPositiveButton("确定", new CreateDialog(textEntryView));
                builder.setNegativeButton("取消", null);
                break;
            case 1:// 按时间排序
                LayoutInflater factory2 = LayoutInflater.from(this);
                final View textEntryView2 = factory2.inflate(R.layout.filedialog,
                        null);
                builder.setTitle("重命名文件");
                builder.setView(textEntryView2);
                builder.setPositiveButton("确定", new RenameDialog(textEntryView2));
                // <span
                // style="font-family:Arial,Helvetica,sans-serif;">builder.setNegativeButton("取消",
                // null);</span>
                builder.setNegativeButton("取消", null);
                break;
            case 2:
                builder.setTitle("确定要删除吗？");
                builder.setPositiveButton("确定", new DeleteDialog());
                builder.setNegativeButton("取消", null);
                break;
        }
        builder.create().show();
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param sPath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public boolean DeleteFolder(String sPath) {
        flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) { // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) { // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else { // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String sPath) {
        flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String sPath) {
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } // 删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 文件刷新
     *
     * @param file
     */
    private void fileScan(String file) {
        Uri data = Uri.parse("file://" + file);

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
    }

    /**
     * 启动文件打开
     *
     * @param f
     */
    private void openFile(File f) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);

        // 获取文件媒体类型
        String type = getMIMEType(f);
        if (type == null)
            return;
        intent.setDataAndType(Uri.fromFile(f), type);
        startActivity(intent);
    }

    // add for test start
    private final String[][] MIME_MapTable = {
            // {后缀名， MIME类型}
            {".bin", "application/octet-stream"}, {".bmp", "image/bmp"},
            {".exe", "application/octet-stream"}, {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".js", "application/x-javascript"},
            {".m3u", "audio/x-mpegurl"}, {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"}, {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"}, {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".msg", "application/vnd.ms-outlook"},
            {".rtf", "application/rtf"}, {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"}};

    // add for test end

    // 获取文件类型
    private String getMIMEType(File file) {
        String type = "";
        String fileName = file.getName();
        String end = fileName.substring(fileName.indexOf(".") + 1)
                .toLowerCase();
        // 判断文件类型
        // 文档文件Text
        if (end.equals("xml") || end.equals("text") || end.equals("h")
                || end.equals("sh") || end.equals("rc") || end.equals("java")
                || end.equals("c") || end.equals("cpp") || end.equals("conf")
                || end.equals("prop") || end.equals("log"))
            type = "text/plain";
        else if (end.equals("htm") || end.equals("html"))
            type = "text/html";
            // 视频文件Video
        else if (end.equals("mpe") || end.equals("mpg") || end.equals("mpeg"))
            type = "video/mpeg";
        else if (end.equals("3gp"))
            type = "video/3gpp";
        else if (end.equals("mp4") || end.equals("mpeg4"))
            type = "video/mp4";
        else if (end.equals("avi"))
            type = "video/x-msvideo";
        else if (end.equals("asf"))
            type = "video/x-ms-asf";
            // 音频文件Audio
        else if (end.equals("mp2") || end.equals("mp3"))
            type = "audio/x-mpeg";
        else if (end.equals("wav"))
            type = "audio/x-wav";
        else if (end.equals("rmvb"))
            type = "audio/x-pn-realaudio";
        else if (end.equals("ogg"))
            type = "audio/ogg";
        else if (end.equals("mpga"))
            type = "audio/mpeg";

            // 图片文件
        else if (end.equals("png"))
            type = "image/png";
        else if (end.equals("jpg") || end.equals("jpeg"))
            type = "image/jpeg";
            // 扩展文件Application
        else if (end.equals("ppt") || end.equals("pps"))
            type = "application/vnd.ms-powerpoint";
        else if (end.equals("doc"))
            type = "application/msword";
        else if (end.equals("wps"))
            type = "application/vnd.ms-works";
        else if (end.equals("apk"))
            type = "application/vnd.android.package-archive";
        else if (end.equals("pdf"))
            type = "application/pdf";
        else if (end.equals("rar"))
            type = "application/x-rar-compressed";
        else if (end.equals("tgz") || end.equals("z"))
            type = "application/x-compressed";
        else if (end.equals("tar"))
            type = "application/x-tar";
        else if (end.equals("zip"))
            type = "application/zip";
        else if (end.equals("gz"))
            type = "application/x-gzip";
        else if (end.equals("jar"))
            type = "application/java-archive";
        else if (end.equals("bin") || end.equals("exe"))
            type = "application/octet-stream";
            // 未知文件
        else if (end.equals(""))
            type = "*/*";
        /*
		 * if (end.equals("wma") || end.equals("mp3") || end.equals("midi") ||
		 * end.equals("ape") || end.equals("amr") || end.equals("ogg") ||
		 * end.equals("wav") || end.equals("acc")) { type = "audio"; } else if
		 * (end.equals("3gp") || end.equals("mp4") || end.equals("rmvb") ||
		 * end.equals("flv") || end.equals("avi") || end.equals("wmv") ||
		 * end.equals("f4v")) { type = "video"; } else if (end.equals("jpg") ||
		 * end.equals("gif") || end.equals("png") || end.equals("jpeg") ||
		 * end.equals("bmp")) { type = "image"; } //add for doc start else
		 * if(end
		 * .equals("txt")||end.equals("cpp")||end.equals("c")||end.equals("java"
		 * )||end.equals("prop")){ type = "text"; } //add for doc end else {
		 * Toast.makeText(getApplicationContext(), "未知文件类型", Toast.LENGTH_LONG)
		 * .show(); return null; }
		 */
        // MIME Type格式是"文件类型/文件扩展名"
        type += "/*";
        return type;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, Menu.FIRST, 0, "新建");
        menu.add(0, Menu.FIRST + 1, 0, "关于");
        menu.add(0, Menu.FIRST + 2, 0, "排序");
        menu.add(0, Menu.FIRST + 3, 0, "退出");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1: // 新建（文件夹）
                myNewDialog(0);
                break;
            case 2: // 关于
                Toast.makeText(this, "Copyright@Tchip 2014", Toast.LENGTH_SHORT)
                        .show();
                break;
            case 3: // 排序
                mySortDialog(0);
                break;
            case 4: // 退出应用
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String[] items = {"重命名", "删除"};

        builder.setItems(items, new LongDialog(arg2));
        builder.create().show();
        return true;
    }

    class CreateDialog implements DialogInterface.OnClickListener {

        private View textEntryView;

        public CreateDialog(View textEntryView) {
            this.textEntryView = textEntryView;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            // EditText userName = (EditText) textEntryView
            // .findViewById(R.id.fname);
            EditText userName = (EditText) textEntryView
                    .findViewById(R.id.fname);
            String fn = userName.getText().toString().trim();

            if (!TextUtils.isEmpty(fn)) {
                File file = new File(backFile, fn);
                if (file.exists()) {
                    Toast.makeText(Main.this, "文件夹已存在", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    if (file.mkdir()) {
                        Toast.makeText(Main.this, "创建成功！", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(Main.this, "创建失败！", Toast.LENGTH_SHORT)
                                .show();
                    }
                    listDir(file.getAbsolutePath());
                    // fileScan(f.getAbsolutePath());
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    class RenameDialog implements DialogInterface.OnClickListener {
        private View textEntryView2;

        public RenameDialog(View textEntryView2) {
            this.textEntryView2 = textEntryView2;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            EditText userName = (EditText) textEntryView2
                    .findViewById(R.id.fname);
            String fn = userName.getText().toString().trim();
            if (!TextUtils.isEmpty(fn)) {
                File old = new File(currentPath);
                File f = new File(backFile.getAbsolutePath(), fn);
                if (f.exists()) {
                    Toast.makeText(Main.this, "文件已存在", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Main.this, "文件重命名 " + old.renameTo(f), Toast.LENGTH_SHORT)
                            .show();
                    listDir(f.getAbsolutePath());
                    // fileScan(f.getAbsolutePath());
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    class DeleteDialog implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            File file = new File(currentPath);
            if (file.exists()) {
                DeleteFolder(file.getAbsolutePath());
                listDir(file.getParent());
                // fileScan(f.getAbsolutePath());
                adapter.notifyDataSetChanged();
            } else
                Toast.makeText(Main.this, "文件不存在！", Toast.LENGTH_SHORT).show();
        }
    }

    // 文件操作选择
    class LongDialog implements DialogInterface.OnClickListener {
        private int pos = 0;

        public LongDialog(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    currentPath = (String) items.get(pos).get("path");
                    myNewDialog(1);
                    break;
                case 1:
                    currentPath = (String) items.get(pos).get("path");
                    myNewDialog(2);
                    break;
            }
        }
    }

}
