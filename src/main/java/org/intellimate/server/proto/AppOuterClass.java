// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: server-protobuf/app.proto

package org.intellimate.server.proto;

public final class AppOuterClass {
  private AppOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  static com.google.protobuf.Descriptors.Descriptor
    internal_static_intellimate_App_descriptor;
  static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_intellimate_App_fieldAccessorTable;
  static com.google.protobuf.Descriptors.Descriptor
    internal_static_intellimate_App_AppVersion_descriptor;
  static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_intellimate_App_AppVersion_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\031server-protobuf/app.proto\022\013intellimate" +
      "\"\342\001\n\003App\022\n\n\002id\030\001 \001(\005\022\014\n\004name\030\002 \001(\t\022\021\n\tde" +
      "veloper\030\003 \001(\t\022\023\n\013description\030\004 \001(\t\022\014\n\004ta" +
      "gs\030\005 \003(\t\022-\n\010versions\030\006 \003(\0132\033.intellimate" +
      ".App.AppVersion\032\\\n\nAppVersion\022\017\n\007version" +
      "\030\001 \001(\t\022\025\n\rdownload_link\030\002 \001(\t\022&\n\014depende" +
      "ncies\030\003 \003(\0132\020.intellimate.AppB \n\034org.int" +
      "ellimate.server.protoP\001b\006proto3"
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
    internal_static_intellimate_App_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_intellimate_App_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_intellimate_App_descriptor,
        new java.lang.String[] { "Id", "Name", "Developer", "Description", "Tags", "Versions", });
    internal_static_intellimate_App_AppVersion_descriptor =
      internal_static_intellimate_App_descriptor.getNestedTypes().get(0);
    internal_static_intellimate_App_AppVersion_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_intellimate_App_AppVersion_descriptor,
        new java.lang.String[] { "Version", "DownloadLink", "Dependencies", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
