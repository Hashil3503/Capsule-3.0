import pandas as pd

in_path  = "건강보험심사평가원_약가마스터_의약품표준코드_20241014.csv"
out_path = "first_column_only.csv"
encoding_in = "cp949"      # 필요시 "utf-8-sig"로 변경
encoding_out = "utf-8-sig" # 결과파일 인코딩

df = pd.read_csv(in_path, encoding=encoding_in)
df.iloc[:, [0]].to_csv(out_path, index=False, encoding=encoding_out)
