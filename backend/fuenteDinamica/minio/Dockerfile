FROM minio/minio

ENV MINIO_ROOT_USER=admin
ENV MINIO_ROOT_PASSWORD=admin123

CMD ["sh", "-c", "minio server /data --address :$PORT --console-address :9001"]

