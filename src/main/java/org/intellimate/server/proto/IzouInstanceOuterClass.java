// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: server-protobuf/izou_instance.proto

package org.intellimate.server.proto;

public final class IzouInstanceOuterClass {
  private IzouInstanceOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_intellimate_IzouInstance_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_intellimate_IzouInstance_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n#server-protobuf/izou_instance.proto\022\013i" +
      "ntellimate\"7\n\014IzouInstance\022\n\n\002id\030\001 \001(\005\022\014" +
      "\n\004name\030\002 \001(\t\022\r\n\005token\030\003 \001(\tB \n\034org.intel" +
      "limate.server.protoP\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_intellimate_IzouInstance_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_intellimate_IzouInstance_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_intellimate_IzouInstance_descriptor,
        new java.lang.String[] { "Id", "Name", "Token", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
