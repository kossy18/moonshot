export interface DataFile {
  getName(): string;

  getPath(): string;

  exists(): boolean;

  isDirectory(): boolean;

  isFile(): boolean;

  length(): number;

  list(): string[];

  mkdir(): boolean;

  delete(): boolean;
}
