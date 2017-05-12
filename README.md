# annotations-sample

> An compile-time annotation framework example for Android（Android编译时注解框架示例项目），目前模仿BufferKinife实现了View注解绑定的功能。

## 框架说明
- viewinject-annotation 用于存放注解，Java模块
- viewinject-compiler 用于编写注解处理器，Java模块
- viewinject-api 用于给外部提供使用的API，Android模块
- viewinject-sample 示例demo，Android模块

## 框架依赖
1. viewinject-sample依赖viewinject-api，使用apt插件编译viewinject-compiler；
2. viewinject-api依赖viewinject-annotation

## 使用示例
使用API，ViewInjector.injectView(object)来实现Activity findViewById(id)重复繁琐的代码，控件使用Bind注解绑定。

```
    @Bind(R.id.id_textview)
    TextView mIdTextView;
    @Bind(R.id.id_btn)
    Button mIdBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        ViewInjector.injectView(this, view);

        mIdTextView.setText("ViewInject");
        mIdBtn.setText("ViewInject ~");

        return view;
    }
``` 

## 外部依赖
-  compile 'com.google.auto.service:auto-service:1.0-rc2'（auto-service库生成META-INF等信息）
-  apply plugin: 'com.neenbedankt.android-apt'（android-apt插件）


