// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: server-protobuf/user.proto

package org.intellimate.server.proto;

public final class UserOuterClass {
  private UserOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_intellimate_User_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_intellimate_User_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\032server-protobuf/user.proto\022\013intellimat" +
      "e\"E\n\004User\022\020\n\010username\030\001 \001(\t\022\r\n\005email\030\002 \001" +
      "(\t\022\020\n\010password\030\003 \001(\t\022\n\n\002id\030\004 \001(\005B \n\034org." +
      "intellimate.server.protoP\001b\006proto3"
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
    internal_static_intellimate_User_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_intellimate_User_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_intellimate_User_descriptor,
        new java.lang.String[] { "Username", "Email", "Password", "Id", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
