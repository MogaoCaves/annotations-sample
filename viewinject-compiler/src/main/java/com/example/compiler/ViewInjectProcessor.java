package com.example.compiler;

import com.example.annotation.Bind;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by Nelson on 17/4/25.
 */

@AutoService(Processor.class)
public class ViewInjectProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    /**
     * 一个集合，存放上述类对象（遍历生成代理类），key为类的全路径
     */
    private Map<String, ProxyInfo> mProxyMap = new HashMap<>();

    /**
     * Filer mFileUtils 跟文件相关的辅助类，生成JavaSourceCode.
     * Elements mElementUtils 跟元素相关的辅助类，帮助我们去获取一些元素相关的信息。
     *  - VariableElement 一般代表成员变量
     *  - ExecutableElement 一般代表类中的方法
     *  - TypeElement 一般代表类
     *  - PackageElement 一般代表Package
     * Messager mMessager 跟日志相关的辅助类。
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    /**
     * 返回支持的注解类型
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(Bind.class.getCanonicalName());
        return supportTypes;
    }

    /**
     * 返回支持的源码版本
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 核心代码
     *  - 收集信息
     *  - 生成代理类（把编译时生成的类叫代理类）
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "process...");
        mProxyMap.clear();

        // 拿到通过@BindView注解的元素，返回值，是VariableElement集合，因为我们作用于成员变量上的！！！
        Set<? extends Element> elesWithBid = roundEnv.getElementsAnnotatedWith(Bind.class);
        // 一、收集信息
        for (Element element : elesWithBid) {
            // 检查element类型，检查类型是否是VariableElement
            checkAnnotationValid(element, Bind.class);
            // field type
            VariableElement variableElement = (VariableElement) element;
            //class type 拿到对应的类的TypeElement，继而生成ProxyInfo对象，通过一个mProxyMap进行检查，
            // key为quifiedName即类的全路径，如果没有生成才会生成一个新的,ProxyInfo与类是一一对应的。
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();// TypeElement
            //full class name
            String fqClassName = classElement.getQualifiedName().toString();

            ProxyInfo proxyInfo = mProxyMap.get(fqClassName);
            if (proxyInfo == null) {
                proxyInfo = new ProxyInfo(elementUtils, classElement);
                mProxyMap.put(fqClassName, proxyInfo);
            }
            // 会将与该类对应的且被@BindView声明的VariableElement加入到ProxyInfo中去，key为我们声明时填写的id，即View的id
            // 这样就生成了信息的收集，收集完成信息后，就可以生成代理类
            Bind bindAnnotation = variableElement.getAnnotation(Bind.class);
            int id = bindAnnotation.value();
            proxyInfo.injectVariables.put(id, variableElement);
        }

        // 生成代理类
        for (String key : mProxyMap.keySet()) {
            ProxyInfo proxyInfo = mProxyMap.get(key);
            try {
                // 生成JavaFileObject
                // 通过mFileUtils.createSourceFile来创建文件对象，类名为proxyInfo.getProxyClassFullName()，写入的内容为proxyInfo.generateJavaCode().
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                        proxyInfo.getProxyClassFullName(),
                        proxyInfo.getTypeElement());
                Writer writer = jfo.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                error(proxyInfo.getTypeElement(),
                        "Unable to write injector for type %s: %s",
                        proxyInfo.getTypeElement(), e.getMessage());
            }
        }


        return false;
    }

    private boolean checkAnnotationValid(Element annotatedElement, Class clazz) {
        if (annotatedElement.getKind() != ElementKind.FIELD) {
            error(annotatedElement, "%s must be declared on filed.", clazz.getSimpleName());
            return false;
        }

        if (ClassValidator.isPrivate(annotatedElement)) {
            error(annotatedElement, "%s() must can not be private.", annotatedElement.getSimpleName());
            return false;
        }

        return true;
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message, element);
    }
}
