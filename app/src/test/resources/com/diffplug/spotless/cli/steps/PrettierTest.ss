╔═ itRunsPrettierForTsFilesWithOptionFile ═╗
export class MyVeryOwnControllerWithARatherLongNameThatIsNotReallyNecessary
  extends AbstractController
  implements
    DisposeAware,
    CallbackAware
{
  public myValue: string[];

  constructor(
    private myService: Service,
    name: string,
    private field: any
  ) {
    super(name);
  }

  //...
}

╔═ itRunsPrettierForTsFilesWithOptions ═╗
export class MyVeryOwnControllerWithARatherLongNameThatIsNotReallyNecessary
  extends AbstractController
  implements
    DisposeAware,
    CallbackAware
{
  public myValue: string[];

  constructor(
    private myService: Service,
    name: string,
    private field: any
  ) {
    super(name);
  }

  //...
}

╔═ itRunsPrettierWithoutAnyOptions ═╗
export class MyVeryOwnControllerWithARatherLongNameThatIsNotReallyNecessary
  extends AbstractController
  implements DisposeAware, CallbackAware
{
  public myValue: string[];

  constructor(private myService: Service, name: string, private field: any) {
    super(name);
  }

  //...
}

╔═ itRunsSpecificPrettierVersion2x ═╗
export class MyVeryOwnControllerWithARatherLongNameThatIsNotReallyNecessary
  extends AbstractController
  implements DisposeAware, CallbackAware
{
  public myValue: string[];

  constructor(private myService: Service, name: string, private field: any) {
    super(name);
  }

  //...
}

╔═ itUsesACacheDir ═╗
export class MyVeryOwnControllerWithARatherLongNameThatIsNotReallyNecessary
  extends AbstractController
  implements DisposeAware, CallbackAware
{
  public myValue: string[];

  constructor(private myService: Service, name: string, private field: any) {
    super(name);
  }

  //...
}

╔═ [end of file] ═╗
