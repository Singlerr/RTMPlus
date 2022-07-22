package io.github.singlerr.rtmplus.patch;

import javassist.*;
import net.minecraft.block.BlockContainer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;

public class PatchApplier implements IClassTransformer {
    private static HashMap<String, byte[]> classBytes = new HashMap<>();


    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        switch (name) {
            case "net.minecraft.block.state.BlockStateContainer":
            case "jp.ngt.ngtlib.block.BlockContainerCustomWithMeta":
            case "jp.ngt.rtm.block.tileentity.TileEntityMachineBase":
            case "net.minecraft.world.World":
            case "net.minecraft.tileentity.TileEntity":
            case "net.minecraft.util.math.BlockPos":
            case "net.minecraft.world.IBlockAccess":
            case "net.minecraft.block.state.IBlockState":
            case "net.minecraft.entity.Entity":
                classBytes.put(name, basicClass);
                break;
        }
        if (name.equals("jp.ngt.rtm.entity.vehicle.EntityVehicleBase")) {
            ClassPool pool = new ClassPool(null);
            pool.appendSystemPath();
            pool.appendClassPath(new ByteArrayClassPath("jp.ngt.rtm.entity.vehicle.EntityVehicleBase", basicClass));
            pool.appendClassPath(new ByteArrayClassPath("net.minecraft.entity.Entity", classBytes.get("net.minecraft.entity.Entity")));
            try {
                CtClass trainBase = pool.get("jp.ngt.rtm.entity.vehicle.EntityVehicleBase");
                CtMethod method = trainBase.getDeclaredMethod("fixRiderPosOnDismount");
                method.setBody("{}");
                return trainBase.toBytecode();
            } catch (NotFoundException | CannotCompileException | IOException e) {
                return basicClass;
            }
        }
        if (name.equals("jp.ngt.rtm.block.BlockMachineBase")) {
            boolean isObfuscated = false;
            try {
                ClassNode classNode = new ClassNode();
                ClassReader classReader = new ClassReader(basicClass);
                classReader.accept(classNode, 0);

                String funcNameGetLightValue = "getLightValue";
                String funcDescGetLightValue = ! isObfuscated ? "(Lawr;Lamw;Let;)I" : "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)I";
                for (MethodNode methodNode : classNode.methods) {
                    if (methodNode.name.equals(funcNameGetLightValue) && methodNode.desc.equals(funcDescGetLightValue)) {
                        for(AbstractInsnNode n : methodNode.instructions.toArray()){
                            if(n instanceof MethodInsnNode){
                                System.out.println(((MethodInsnNode) n).desc);
                                System.out.println(((MethodInsnNode) n).name);
                            }

                            
                        }
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
                        list.add(new VarInsnNode(Opcodes.ALOAD, 3));
                        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, isObfuscated ? "amw" : "net/minecraft/world/IBlockAccess", isObfuscated ? "r" : "getTileEntity", isObfuscated ? "(Let;)Lavh" : "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/tileentity/TileEntity", true));
                        list.add(new TypeInsnNode(Opcodes.INSTANCEOF, "jp/ngt/rtm/block/tileentity/TileEntityMachineBase"));
                        list.add(new JumpInsnNode(Opcodes.IFNE, (LabelNode) methodNode.instructions.getFirst()));
                        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "jp/ngt/ngtlib/block/BlockContainerCustomWithMeta", "o", isObfuscated ? "(Lawr;)I" : "(Lnet/minecraft/block/state/IBlockState;)I", false));
                        list.add(new InsnNode(Opcodes.IRETURN));
                        if(isObfuscated)
                            return null;
                        methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), list);

                        break;
                    }

                }
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                classNode.accept(classWriter);
                return classWriter.toByteArray();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        return basicClass;
    }
}
     /*
        if (name.equals("jp.ngt.rtm.block.BlockMachineBase")) {
            ClassPool pool = new ClassPool(null);
            pool.appendSystemPath();
            pool.importPackage("net.minecraft.tileentity.TileEntity");
            pool.appendClassPath(new ByteArrayClassPath("net.minecraft.block.state.BlockStateContainer",classBytes.get("net.minecraft.block.state.BlockStateContainer")));
            pool.appendClassPath(new ByteArrayClassPath("jp.ngt.ngtlib.block.BlockContainerCustomWithMeta",classBytes.get("jp.ngt.ngtlib.block.BlockContainerCustomWithMeta")));
            pool.appendClassPath(new ByteArrayClassPath("jp.ngt.rtm.block.tileentity.TileEntityMachineBase",classBytes.get("jp.ngt.rtm.block.tileentity.TileEntityMachineBase")));
            pool.appendClassPath(new ByteArrayClassPath("jp.ngt.rtm.block.BlockMachineBase", basicClass));
            pool.appendClassPath(new ByteArrayClassPath("net.minecraft.world.World",classBytes.get("net.minecraft.world.World")));
            pool.appendClassPath(new ByteArrayClassPath("net.minecraft.tileentity.TileEntity", classBytes.get("net.minecraft.tileentity.TileEntity")));
            pool.appendClassPath(new ByteArrayClassPath("net.minecraft.util.math.BlockPos", classBytes.get("net.minecraft.util.math.BlockPos")));
            pool.appendClassPath(new ByteArrayClassPath("net.minecraft.world.IBlockAccess", classBytes.get("net.minecraft.world.IBlockAccess")));
            pool.appendClassPath(new ByteArrayClassPath("net.minecraft.block.state.IBlockState", classBytes.get("net.minecraft.block.state.IBlockState")));
            try {
                CtClass cls = pool.get("jp.ngt.rtm.block.BlockMachineBase");
                CtMethod method = cls.getDeclaredMethod("getLightValue");
                method.setBody("{ Object ent = $1.func_175625($2);\n" +
                        "      if (!(ent instanceof TileEntityMachineBase)) return BlockStateContainer.this.getLightValue($0);\n" +
                        "      TileEntityMachineBase tileentitymachinebase = (TileEntityMachineBase)ent;\n" +
                        "      if (tileentitymachinebase == null) {\n" +
                        "         return 0;\n" +
                        "      } else {\n" +
                        "         MachineConfig machineconfig = tileentitymachinebase.getResourceState().getResourceSet().getConfig();\n" +
                        "         return tileentitymachinebase.isGettingPower ? machineconfig.brightness[1] : machineconfig.brightness[0];\n" +
                        "      }}");




                return cls.toBytecode();
            } catch (NotFoundException | CannotCompileException | IOException e) {
                e.printStackTrace();
                return basicClass;
            }

         */