// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: server-protobuf/http_request.proto

package org.intellimate.server.proto;

public final class HttpRequestOuterClass {
  private HttpRequestOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  static com.google.protobuf.Descriptors.Descriptor
    internal_static_intellimate_HttpRequest_descriptor;
  static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_intellimate_HttpRequest_fieldAccessorTable;
  static com.google.protobuf.Descriptors.Descriptor
    internal_static_intellimate_HttpRequest_Param_descriptor;
  static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_intellimate_HttpRequest_Param_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\"server-protobuf/http_request.proto\022\013in" +
      "tellimate\"\243\001\n\013HttpRequest\022\013\n\003url\030\001 \001(\t\022." +
      "\n\006params\030\002 \003(\0132\036.intellimate.HttpRequest" +
      ".Param\022\016\n\006method\030\003 \001(\t\022\024\n\014content_type\030\004" +
      " \001(\t\022\014\n\004body\030\005 \001(\014\032#\n\005Param\022\013\n\003key\030\001 \001(\t" +
      "\022\r\n\005value\030\002 \003(\tB \n\034org.intellimate.serve" +
      "r.protoP\001b\006proto3"
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
    internal_static_intellimate_HttpRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_intellimate_HttpRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_intellimate_HttpRequest_descriptor,
        new java.lang.String[] { "Url", "Params", "Method", "ContentType", "Body", });
    internal_static_intellimate_HttpRequest_Param_descriptor =
      internal_static_intellimate_HttpRequest_descriptor.getNestedTypes().get(0);
    internal_static_intellimate_HttpRequest_Param_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_intellimate_HttpRequest_Param_descriptor,
        new java.lang.String[] { "Key", "Value", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
